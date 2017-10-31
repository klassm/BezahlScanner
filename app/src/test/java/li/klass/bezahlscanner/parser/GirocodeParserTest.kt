package li.klass.bezahlscanner.parser

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.klass.bezahlscanner.DateTimeProvider
import li.klass.bezahlscanner.Payment
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Test

class GirocodeParserTest {
    @Test
    fun should_parse() {
        val toParse = "BCD\n" +
                "001\n" +
                "1\n" +
                "SCT\n" +
                "AUGSDE77XXX\n" +
                "Matthias Klass\n" +
                "DE11720500000000061390\n" +
                "EUR123\n" +
                "CASH\n" +
                "\n" +
                "Test123"
        val now = DateTime.now()
        val dateTimeProvider = mock<DateTimeProvider> { on { now() } doReturn now }
        val parser = GirocodeParser(dateTimeProvider)

        assertThat(parser.canParse(toParse)).isTrue()
        assertThat(parser.parse(toParse)).isEqualTo(Payment(
                iban = "DE11720500000000061390",
                amount = "123",
                currency = "EUR",
                bic = "AUGSDE77XXX",
                date = now,
                name = "Matthias Klass",
                reason = "Test123"
        ))
    }


    @Test
    fun should_parse_without_reason() {
        val toParse = "BCD\n" +
                "001\n" +
                "2\n" +
                "SCT\n" +
                "SOLADES1PFD\n" +
                "Girosolution GmbH\n" +
                "DE19690516200000581900\n" +
                "EUR1"

        val now = DateTime.now()
        val dateTimeProvider = mock<DateTimeProvider> { on { now() } doReturn now }
        val parser = GirocodeParser(dateTimeProvider)

        assertThat(parser.canParse(toParse)).isTrue()
        assertThat(parser.parse(toParse)).isEqualTo(Payment(
                iban = "DE19690516200000581900",
                amount = "1",
                currency = "EUR",
                bic = "SOLADES1PFD",
                date = now,
                name = "Girosolution GmbH",
                reason = ""
        ))
    }
}