package li.klass.bezahl.scanner.parser

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.klass.bezahl.scanner.DateTimeProvider
import li.klass.bezahl.scanner.Payment
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Test

class GirocodeParserTest {
    @Test
    fun should_parse() {
        val toParse = """
            BCD
            001
            1
            SCT
            AUGSDE77XXX
            Matthias Klass
            DE11720500000000061390
            EUR123
            CASH

            Test123""".trimIndent()
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
        val toParse = """
            BCD
            001
            2
            SCT
            SOLADES1PFD
            Girosolution GmbH
            DE19690516200000581900
            EUR1""".trimIndent()

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

    @Test
    fun should_parse_minimal() {
        val toParse = """
            BCD
            001
            1
            SCT
            BIC
            John Doe
            IBAN
        """.trimIndent()

        val (now, result) = parse(toParse)
        assertThat(result).isEqualTo(Payment(
                iban = "IBAN",
                amount = "",
                currency = "EUR",
                bic = "BIC",
                date = now,
                name = "John Doe",
                reason = ""
        ))
    }

    private fun parse(toParse: String): Pair<DateTime, Payment?> {
        val now = DateTime.now()
        val dateTimeProvider = mock<DateTimeProvider> { on { now() } doReturn now }
        val parser = GirocodeParser(dateTimeProvider)

        assertThat(parser.canParse(toParse)).isTrue()
        val result = parser.parse(toParse)
        return Pair(now, result)
    }

    @Test
    fun should_parse_something() {
        val toParse = """
            BCD
            001
            8
            SCT
            BIC
            Name
            IBAN
            EUR188.5


            BlaNummer BlaZweck
            """.trimIndent()

        val (now, result) = parse(toParse)
        assertThat(result).isEqualTo(Payment(
                iban = "IBAN",
                amount = "188.5",
                currency = "EUR",
                bic = "BIC",
                date = now,
                name = "Name",
                reason = "BlaNummer BlaZweck"
        ))
    }
}