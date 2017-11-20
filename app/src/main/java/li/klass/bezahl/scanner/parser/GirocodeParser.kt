package li.klass.bezahl.scanner.parser

import li.klass.bezahl.scanner.DateTimeProvider
import li.klass.bezahl.scanner.Payment
import java.util.*

class GirocodeParser(val dateTimeProvider: DateTimeProvider) : QrCodeParser {
    override fun parse(value: String): Payment? {
        val lines = value.split("[\r\n]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // Field 'reason' after first empty line
        val indexEmptyLine = Arrays.asList(*lines).indexOf("")

        return Payment(
                date = dateTimeProvider.now(),
                bic = lines.getOrElse(4, { "" }),
                name = lines.getOrElse(5, { "" }),
                iban = lines.getOrElse(6, { "" }),
                amount = lines.getOrElse(7, { "" }).replace("[A-Za-z]+".toRegex(), ""),
                currency = lines.getOrElse(7, { "EUR" }).replace("[^A-Za-z]+".toRegex(), ""),
                reason = lines.getOrElse(if (indexEmptyLine > 0) indexEmptyLine + 1 else 10, { "" })
        )
    }

    override fun canParse(value: String): Boolean = value.startsWith("BCD")
}