package li.klass.bezahlscanner.parser

import li.klass.bezahlscanner.DateTimeProvider
import li.klass.bezahlscanner.Payment
import java.util.*

class GirocodeParser(val dateTimeProvider: DateTimeProvider) : QrCodeParser {
    override fun parse(value: String): Payment? {
        val lines = value.split("[\r\n]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (lines.size < 7) {
            return null
        }

        // Field 'reason' after first empty line
        val indexEmptyLine = Arrays.asList(*lines).indexOf("")

        return Payment(
                date = dateTimeProvider.now(),
                bic = lines[4],
                name = lines[5],
                iban = lines[6],
                amount = lines[7].replace("[A-Za-z]+".toRegex(), ""),
                currency = lines[7].replace("[^A-Za-z]+".toRegex(), ""),
                reason = if (lines.size < 10) "" else lines[if (indexEmptyLine > 0) indexEmptyLine + 1 else 10])
    }

    override fun canParse(value: String): Boolean = value.startsWith("BCD")
}