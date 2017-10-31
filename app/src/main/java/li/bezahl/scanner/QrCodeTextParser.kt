package li.bezahl.scanner

import li.klass.bezahlscanner.parser.Parsers
import org.apache.commons.lang3.StringUtils

class QrCodeTextParser {

    fun parse(text: String?): Payment? {
        val toParse = StringUtils.trimToNull(text)
        toParse ?: return null

        return Parsers.allParsers.firstOrNull { it.canParse(toParse) }?.parse(toParse)
    }
}
