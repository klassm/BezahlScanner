package li.klass.bezahl.scanner.parser

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.bezahl.scanner.DateTimeProvider
import li.bezahl.scanner.Payment
import li.klass.bezahlscanner.parser.BezahlCodeParser
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Test

class BezahlCodeParserTest {
    @Test
    fun should_parse() {
        val toParse = "bank://singlepaymentsepa?name=MATTHIAS%20KLASS&reason=Test123&iban=DE11720500000000061390&bic=AUGSDE77XXX&amount=123"
        val now = DateTime.now()
        val dateTimeProvider = mock<DateTimeProvider> { on { now() } doReturn now }

        val parser = BezahlCodeParser(dateTimeProvider)

        assertThat(parser.canParse(toParse)).isTrue()
        assertThat(parser.parse(toParse)).isEqualTo(Payment(
                iban = "DE11720500000000061390",
                amount = "123",
                currency = "EUR",
                bic = "AUGSDE77XXX",
                date = now,
                name = "MATTHIAS KLASS",
                reason = "Test123"
        ))
    }

    @Test
    fun should_parse_empty() {
        val toParse = "bank://singlepaymentsepa?name=&reason=&iban=&bic=&amount="
        val now = DateTime.now()
        val dateTimeProvider = mock<DateTimeProvider> { on { now() } doReturn now }

        val parser = BezahlCodeParser(dateTimeProvider)

        assertThat(parser.canParse(toParse)).isTrue()
        assertThat(parser.parse(toParse)).isEqualTo(Payment(
                iban = "",
                amount = "",
                currency = "EUR",
                bic = "",
                date = now,
                name = "",
                reason = ""
        ))
    }
}