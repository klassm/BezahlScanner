package li.klass.bezahlscanner.parser

import li.klass.bezahlscanner.DateTimeProvider

object Parsers {
    private val dateTimeProvider = DateTimeProvider()
    val allParsers = listOf(
            GirocodeParser(dateTimeProvider),
            SinglePaymentParser(dateTimeProvider),
            SwissQrCodeParser(dateTimeProvider)
    )
}