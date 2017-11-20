package li.klass.bezahl.scanner.parser

import li.klass.bezahl.scanner.DateTimeProvider

object Parsers {
    private val dateTimeProvider = DateTimeProvider()
    val allParsers = listOf(
            GirocodeParser(dateTimeProvider),
            BezahlCodeParser(dateTimeProvider),
            SwissQrCodeParser(dateTimeProvider)
    )
}