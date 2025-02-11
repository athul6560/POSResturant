import com.zeezaglobal.posresturant.Entities.Sale
import java.io.ByteArrayOutputStream

class Token(private val saleItem: Sale) {

    private val format =
        java.text.SimpleDateFormat("dd-MMMM-yy hh:mm a", java.util.Locale.getDefault())

    fun generateTokenStream(): ByteArrayOutputStream {
        val currentDate = format.format(java.util.Date())
        val outputStream = ByteArrayOutputStream()

        // ESC/POS commands for bold and larger text
        val cc = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0x00.toByte()) // Normal size text
        val bb = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0x08.toByte()) // Bold text
        val bb3 = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0x10.toByte()) // Bold with large text

        // Utility function for centering text
        fun centerText(text: String, lineWidth: Int = 32): String {
            val padding = (lineWidth - text.length) / 2
            return " ".repeat(maxOf(0, padding)) + text
        }

        try {
            // Write header centered and bold
            outputStream.write(bb3)
            outputStream.write(centerText("Bean Barrel Token").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(cc)

            outputStream.write(centerText(currentDate).toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(centerText("Tel: +91 92077 78777").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(centerText("supportus@beanbarrel.in").toByteArray())
            outputStream.write("\n\n".toByteArray())

            // Print token number in bold and large text
            outputStream.write(bb3)
            outputStream.write(centerText("Token: ${saleItem.tokenNumber}").toByteArray())
            outputStream.write("\n\n".toByteArray())

            // Print customer details
            outputStream.write(bb)
            outputStream.write("Name: ${saleItem.customerName}".toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write("Bill: ${saleItem.billNumber}".toByteArray())
            outputStream.write("\n\n".toByteArray())

            // Footer message
            outputStream.write(cc)
            outputStream.write(centerText("Please wait for your order!").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(centerText("Thank you for choosing us!").toByteArray())
            outputStream.write("\n".toByteArray())
            outputStream.write(centerText("******************************").toByteArray())
            outputStream.write("\n\n\n".toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outputStream
    }
}
