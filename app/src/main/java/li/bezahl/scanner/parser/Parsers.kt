package li.klass.bezahlscanner.parser

import li.bezahl.scanner.DateTimeProvider

object Parsers {
    private val dateTimeProvider = DateTimeProvider()
    val allParsers = listOf(
            GirocodeParser(dateTimeProvider),
            BezahlCodeParser(dateTimeProvider),
            SwissQrCodeParser(dateTimeProvider)
    )
}