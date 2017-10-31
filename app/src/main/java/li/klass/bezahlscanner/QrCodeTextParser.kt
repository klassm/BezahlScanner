package li.klass.bezahlscanner

import com.google.common.base.Charsets
import org.apache.commons.lang3.StringUtils
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*

class QrCodeTextParser(private val dateTimeProvider: DateTimeProvider) {

    fun parse(text: String?): Payment? {
        val toParse = StringUtils.trimToNull(text)
        toParse ?: return null

        return if (toParse.startsWith(SINGLEPAYMENT_PREFIX)) {
            parseSinglePayments(toParse)
        } else if (toParse.startsWith("BCD")) {
            parseGirocode(toParse)
        } else {
            null
        }
    }

    private fun parseGirocode(text: String): Payment? {
        val lines = text.split("[\r\n]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (lines.size < 10) {
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
                reason = lines[if (indexEmptyLine > 0) indexEmptyLine + 1 else 10])
    }

    private fun parseSinglePayments(text: String): Payment {
        var toParse = text
        val charset = Charsets.UTF_8.displayName()
        try {
            toParse = URLDecoder.decode(toParse, charset)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }

        val parts = toParse.replace(SINGLEPAYMENT_PREFIX, "").split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        var name: String? = null
        var reason: String? = null
        var iban: String? = null
        var bic: String? = null
        var amount: String? = null

        for (part in parts) {
            val elements = part.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val key = elements[0]
            val value = elements[1]
            when (key) {
                "name" -> name = value
                "reason" -> reason = value
                "iban" -> iban = value
                "bic" -> bic = value
                "amount" -> amount = value
            }
        }

        return Payment(
                name = name!!,
                reason = reason!!,
                iban = iban!!,
                bic = bic!!,
                amount = amount!!,
                date = dateTimeProvider.now(),
                currency = "EUR"
        )
    }

    companion object {
        val SINGLEPAYMENT_PREFIX = "bank://singlepaymentsepa?"
    }
}
