package li.klass.bezahl.scanner.parser

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.klass.bezahl.scanner.DateTimeProvider
import li.klass.bezahl.scanner.Payment
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Test

class SwissQrCodeParserTest {
    @Test
    fun should_parse() {
        val toParse = """
            SPC
            0100
            1
            CH4431999123000889012
            Robert Schneider AG
            Rue du Lac
            1268/2/22
            2501
            Biel
            CH
            Robert Schneider Services Switzerland AG
            Rue du Lac
            1268/3/1
            2501
            Biel
            CH
            1 499.95
            CHF

            Pia-Maria Rutschmann-Schnyder
            Grosse Marktgasse
            28
            9400
            Rorschach
            CH
            QRR
            210000000003139471430009017
            QWERTY
            """.trimIndent()
        val now = DateTime.now()
        val dateTimeProvider = mock<DateTimeProvider> { on { now() } doReturn now }
        val parser = SwissQrCodeParser(dateTimeProvider)

        assertThat(parser.canParse(toParse)).isTrue()
        assertThat(parser.parse(toParse)).isEqualTo(Payment(
                iban = "CH4431999123000889012",
                amount = "1499.95",
                currency = "CHF",
                bic = "",
                date = now,
                name = "Robert Schneider Services Switzerland AG",
                reason = "QWERTY\n210000000003139471430009017"
        ))
    }


}