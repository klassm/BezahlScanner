package li.klass.bezahlscanner.parser

import li.bezahl.scanner.Payment

interface QrCodeParser {
    fun canParse(value: String): Boolean
    fun parse(value: String): Payment?
}