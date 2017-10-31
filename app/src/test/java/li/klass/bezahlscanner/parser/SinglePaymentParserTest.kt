package li.klass.bezahlscanner.parser

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.klass.bezahlscanner.DateTimeProvider
import li.klass.bezahlscanner.Payment
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Test

class SinglePaymentParserTest {
    @Test
    fun should_parse() {
        val toParse = "bank://singlepaymentsepa?name=MATTHIAS%20KLASS&reason=Test123&iban=DE11720500000000061390&bic=AUGSDE77XXX&amount=123"
        val now = DateTime.now()
        val dateTimeProvider = mock<DateTimeProvider> { on { now() } doReturn now }

        val parser = SinglePaymentParser(dateTimeProvider)

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
}