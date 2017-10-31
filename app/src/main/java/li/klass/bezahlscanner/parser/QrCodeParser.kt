package li.klass.bezahlscanner.parser

import li.klass.bezahlscanner.Payment

interface QrCodeParser {
    fun canParse(value: String): Boolean
    fun parse(value: String): Payment?
}