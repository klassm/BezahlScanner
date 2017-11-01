package li.klass.bezahlscanner.parser

import com.google.common.base.Charsets
import li.bezahl.scanner.DateTimeProvider
import li.bezahl.scanner.Payment
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class BezahlCodeParser(val dateTimeProvider: DateTimeProvider) : QrCodeParser {
    override fun canParse(value: String): Boolean = value.startsWith(SINGLEPAYMENT_PREFIX)

    override fun parse(value: String): Payment? {
        var toParse = value
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
            val splitKey = elements.getOrElse(0, { "" })
            val splitValue = elements.getOrElse(1, { "" })
            when (splitKey) {
                "name" -> name = splitValue
                "reason" -> reason = splitValue
                "iban" -> iban = splitValue
                "bic" -> bic = splitValue
                "amount" -> amount = splitValue
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