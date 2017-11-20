package li.klass.bezahl.scanner.parser

import li.klass.bezahl.scanner.Payment

interface QrCodeParser {
    fun canParse(value: String): Boolean
    fun parse(value: String): Payment?
}