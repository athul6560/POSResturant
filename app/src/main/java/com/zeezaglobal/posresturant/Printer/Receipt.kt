import com.zeezaglobal.posresturant.Entities.CartItem
import com.zeezaglobal.posresturant.Entities.Sale
import java.io.ByteArrayOutputStream

class Receipt(private val saleItem: Sale) {

    private val format =
        java.text.SimpleDateFormat("dd-MMMM-yy hh:mm a", java.util.Locale.getDefault())

    fun generateReceiptStream(): ByteArrayOutputStream {
        val currentDate = format.format(java.util.Date())
        val outputStream = ByteArrayOutputStream()

        // ESC/POS commands for bold text
        val cc = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0x00.toByte()) // Normal size text
        val bb = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0x08.toByte()) // Bold text
        val bb2 = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0x20.toByte()) // Bold with medium text
        val bb3 = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0x10.toByte()) // Bold with large text

        // Utility function for centering text
        fun centerText(text: String, lineWidth: Int = 32): String {
            val padding = (lineWidth - text.length) / 2
            return " ".repeat(maxOf(0, padding)) + text
        }

        // Utility function for right-aligning text
        fun rightAlignText(text: String, lineWidth: Int = 32): String {
            val padding = lineWidth - text.length
            return " ".repeat(maxOf(0, padding)) + text
        }

        try {
            // Write header centered and bold
            outputStream.write(bb3)
            outputStream.write(centerText("Bean Barrel").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(cc)

            outputStream.write(centerText("$currentDate\n").toByteArray())
            outputStream.write(centerText("Tel: +91 92077 78777").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(centerText("supportus@beanbarrel.in").toByteArray())
            outputStream.write("\n\n".toByteArray())

            outputStream.write("Bill: ${saleItem.billNumber}".toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write("Token: ${saleItem.tokenNumber}".toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write("Name: ${saleItem.customerName}".toByteArray())
            outputStream.write("\n\n".toByteArray())
            outputStream.write("Items Ordered:\n\n".toByteArray())

            var subtotal = 0.0

            saleItem.items.forEachIndexed { index, cartItem ->
                val itemName = cartItem.item.itemName
                val itemPrice = cartItem.item.itemPrice
                val quantity = cartItem.quantity
                val totalPrice = itemPrice * quantity

                subtotal += totalPrice

                val itemLine = "${index + 1}. $itemName\n"
                val priceLine = "Rs.${itemPrice.toInt()} x $quantity    ${
                    rightAlignText(
                        "Rs.${totalPrice.toInt()}",
                        16
                    )
                }\n"

                outputStream.write(itemLine.toByteArray())
                outputStream.write(priceLine.toByteArray())
            }

            // Write subtotal and total
            val total = subtotal
            outputStream.write("\n------------------------------\n".toByteArray())

            outputStream.write(bb)
            outputStream.write(rightAlignText("Total: Rs.${total.toInt()}", 32).toByteArray())
            outputStream.write("\n\n".toByteArray())
            outputStream.write(cc)

            outputStream.write(centerText("Thank you for dining with us!").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(centerText("www.zeezaglobal.com").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(centerText("******************************").toByteArray())
            outputStream.write("\n\n\n".toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outputStream
    }
}
