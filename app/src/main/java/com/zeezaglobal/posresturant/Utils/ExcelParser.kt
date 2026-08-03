package com.zeezaglobal.posresturant.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.util.zip.ZipInputStream

private const val TAG = "ExcelParser"

/**
 * Lightweight .xlsx parser using only Android built-ins (ZipInputStream + XmlPullParser).
 * No Apache POI required — works on all API levels.
 *
 * Excel structure expected:
 *   Row 0 (header): CATEGORY | ITEM NAME | <size1> | <size2> | ...
 *   Rows 1+:        HOT BEV  | Cappuccino | 120     | 130     | ...
 *
 * Each non-empty price cell produces one Item: "Cappuccino - 7oz" @ 120.0
 * Returns: LinkedMap of categoryName → list of (itemName, price)
 */
object ExcelParser {

    fun parse(context: Context, uri: Uri): Map<String, MutableList<Pair<String, Double>>> {
        Log.d(TAG, "Starting Excel parse for uri: $uri")

        // Read the two relevant zip entries into memory (order is not guaranteed in xlsx)
        val entries = mutableMapOf<String, ByteArray>()
        context.contentResolver.openInputStream(uri)!!.use { input ->
            ZipInputStream(input).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    Log.v(TAG, "ZIP entry found: ${entry.name}")
                    if (entry.name == "xl/sharedStrings.xml" ||
                        entry.name == "xl/worksheets/sheet1.xml"
                    ) {
                        entries[entry.name] = zip.readBytes()
                        Log.d(TAG, "Loaded ZIP entry: ${entry.name} (${entries[entry.name]!!.size} bytes)")
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }

        if (!entries.containsKey("xl/sharedStrings.xml")) Log.w(TAG, "No sharedStrings.xml found — all cells may be treated as numeric")
        if (!entries.containsKey("xl/worksheets/sheet1.xml")) {
            Log.e(TAG, "sheet1.xml not found in xlsx — cannot parse")
            return emptyMap()
        }

        val sharedStrings = entries["xl/sharedStrings.xml"]
            ?.let { parseSharedStrings(it) } ?: emptyList()
        Log.d(TAG, "Shared strings loaded: ${sharedStrings.size} entries")

        val sheetBytes = entries["xl/worksheets/sheet1.xml"]!!
        return parseSheet(sheetBytes, sharedStrings)
    }

    // ── Shared strings ────────────────────────────────────────────────────────

    private fun parseSharedStrings(data: ByteArray): List<String> {
        Log.d(TAG, "Parsing sharedStrings.xml...")
        val result = mutableListOf<String>()
        val parser = Xml.newPullParser()
        parser.setInput(data.inputStream(), "UTF-8")

        var inSi = false
        val current = StringBuilder()

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> if (parser.name == "si") {
                    inSi = true; current.clear()
                }
                XmlPullParser.TEXT -> if (inSi) current.append(parser.text)
                XmlPullParser.END_TAG -> if (parser.name == "si") {
                    val str = current.toString().trim()
                    result.add(str)
                    Log.v(TAG, "  SharedString[${result.size - 1}] = \"$str\"")
                    inSi = false
                }
            }
            event = parser.next()
        }
        Log.d(TAG, "sharedStrings.xml parsed: ${result.size} strings")
        return result
    }

    // ── Sheet data ────────────────────────────────────────────────────────────

    private fun parseSheet(
        data: ByteArray,
        sharedStrings: List<String>
    ): Map<String, MutableList<Pair<String, Double>>> {

        val parser = Xml.newPullParser()
        parser.setInput(data.inputStream(), "UTF-8")

        Log.d(TAG, "Parsing sheet1.xml...")
        val rows = mutableListOf<List<String>>()
        var currentRow = mutableListOf<String>()
        var currentRowNum = -1
        var cellType = ""
        var cellValue = ""
        var cellRef = ""
        var cellColIndex = -1
        var inV = false

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "row" -> {
                        currentRow = mutableListOf()
                        currentRowNum = parser.getAttributeValue(null, "r")?.toIntOrNull() ?: -1
                    }
                    "c" -> {
                        cellRef = parser.getAttributeValue(null, "r") ?: ""
                        cellColIndex = colLetterToIndex(cellRef.takeWhile { it.isLetter() })
                        cellType = parser.getAttributeValue(null, "t") ?: ""
                        cellValue = ""
                    }
                    "v" -> inV = true
                }
                XmlPullParser.TEXT -> if (inV) cellValue += parser.text
                XmlPullParser.END_TAG -> when (parser.name) {
                    "v" -> inV = false
                    "c" -> {
                        val resolved = if (cellType == "s") {
                            val idx = cellValue.toIntOrNull() ?: -1
                            if (idx in sharedStrings.indices) sharedStrings[idx] else ""
                        } else {
                            cellValue
                        }
                        // Pad with empty strings for any skipped columns
                        repeat((cellColIndex + 1) - currentRow.size) { currentRow.add("") }
                        currentRow[cellColIndex] = resolved
                        Log.v(TAG, "  Cell[$cellRef] type=\"$cellType\" rawValue=\"$cellValue\" → \"$resolved\"")
                    }
                    "row" -> {
                        Log.d(TAG, "Row $currentRowNum parsed: $currentRow")
                        rows.add(currentRow.toList())
                    }
                }
            }
            event = parser.next()
        }
        Log.d(TAG, "sheet1.xml parsed: ${rows.size} rows total")

        if (rows.isEmpty()) {
            Log.e(TAG, "No rows found in sheet — aborting")
            return emptyMap()
        }

        // ── Map header columns ────────────────────────────────────────────────
        val header = rows[0]
        Log.d(TAG, "Header row: $header")
        val categoryIdx = header.indexOfFirst { it.equals("CATEGORY", ignoreCase = true) }
        val itemNameIdx = header.indexOfFirst { it.equals("ITEM NAME", ignoreCase = true) }

        if (categoryIdx < 0) { Log.e(TAG, "CATEGORY column not found in header: $header"); return emptyMap() }
        if (itemNameIdx < 0) { Log.e(TAG, "ITEM NAME column not found in header: $header"); return emptyMap() }

        val sizeColumns = header.mapIndexedNotNull { idx, name ->
            if (idx != categoryIdx && idx != itemNameIdx && name.isNotEmpty()) idx to name else null
        }
        Log.d(TAG, "CATEGORY col=$categoryIdx, ITEM NAME col=$itemNameIdx, size columns=${sizeColumns.map { it.second }}")

        // ── Build menu map ────────────────────────────────────────────────────
        val menuMap = linkedMapOf<String, MutableList<Pair<String, Double>>>()
        var skippedRows = 0
        for ((rowIndex, row) in rows.drop(1).withIndex()) {
            val category = row.getOrElse(categoryIdx) { "" }.trim()
            val itemName = row.getOrElse(itemNameIdx) { "" }.trim()
            if (category.isEmpty() || itemName.isEmpty()) {
                Log.v(TAG, "  Data row ${rowIndex + 2} skipped (empty category or item name)")
                skippedRows++
                continue
            }

            val items = menuMap.getOrPut(category) { mutableListOf() }
            sizeColumns.forEach { (colIdx, sizeName) ->
                val raw = row.getOrElse(colIdx) { "" }
                val price = raw.toDoubleOrNull()
                if (price == null || price <= 0) {
                    Log.v(TAG, "  Row ${rowIndex + 2} [$itemName / $sizeName]: no price (raw=\"$raw\") — skipped")
                } else {
                    val label = "$itemName - $sizeName"
                    items.add(label to price)
                    Log.d(TAG, "  Parsed item: \"$label\" @ $price in category \"$category\"")
                }
            }
        }

        Log.d(TAG, "Parse complete — ${menuMap.size} categories, ${menuMap.values.sumOf { it.size }} items, $skippedRows rows skipped")
        menuMap.forEach { (cat, items) -> Log.d(TAG, "  [$cat]: ${items.size} items") }
        return menuMap
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Converts Excel column letters (A, B, … Z, AA, …) to a 0-based index. */
    private fun colLetterToIndex(letters: String): Int {
        var index = 0
        for (ch in letters.uppercase()) index = index * 26 + (ch - 'A' + 1)
        return index - 1
    }
}
