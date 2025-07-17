package com.zeezaglobal.posresturant.ui.UploadExcel

import android.content.Context
import android.net.Uri
import com.zeezaglobal.posresturant.Entities.MenuItem

import org.apache.poi.xssf.usermodel.XSSFWorkbook

fun parseExcelFile(context: Context, uri: Uri): List<MenuItem> {
    val menuList = mutableListOf<MenuItem>()
    val inputStream = context.contentResolver.openInputStream(uri)
    val workbook = XSSFWorkbook(inputStream)
    val sheet = workbook.getSheetAt(0)

    for (row in sheet.drop(1)) {
        val category = row.getCell(0)?.toString()?.trim() ?: ""
        val itemName = row.getCell(1)?.toString()?.trim() ?: ""


        val descriptionCell = row.getCell(2)?.toString()?.trim() ?: ""
        val priceCell = row.getCell(3)

        val price = when (priceCell?.cellType) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> priceCell.numericCellValue
            org.apache.poi.ss.usermodel.CellType.STRING -> priceCell.stringCellValue.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }

        menuList.add(MenuItem(itemName = itemName, price = price, category = category,description = descriptionCell))
    }

    workbook.close()
    inputStream?.close()
    return menuList
}