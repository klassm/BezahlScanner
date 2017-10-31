package li.klass.bezahlscanner.parser

import li.klass.bezahlscanner.DateTimeProvider
import li.klass.bezahlscanner.Payment

class SwissQrCodeParser(val dateTimeProvider: DateTimeProvider) : QrCodeParser {
    override fun canParse(value: String): Boolean = value.startsWith("SPC")
    override fun parse(value: String): Payment? {
        val lines = value.split("\n")

        return Payment(
                iban = lines[3],
                amount = lines[16].replace(" ", ""),
                currency = lines[17],
                bic = "",
                date = dateTimeProvider.now(),
                reason = lines[27] + "\n" + lines[26],
                name = lines[10]
        )
    }
}