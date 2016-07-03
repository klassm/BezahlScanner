package li.klass.bezahlscanner;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.tngtech.java.junit.dataprovider.DataProviders.$;
import static com.tngtech.java.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(DataProviderRunner.class)
public class QrCodeTextParserTest {

    public static final DateTime NOW = DateTime.now();

    @DataProvider
    public static Object[][] provider() {
        return $$(
                $(girocode())
        );
    }

    private static TestCase girocode() {
        return new TestCase("girocode")
                .withText("BCD\n" +
                        "001\n" +
                        "1\n" +
                        "SCT\n" +
                        "AUGSDE77XXX\n" +
                        "Matthias Klass\n" +
                        "DE11720500000000061390\n" +
                        "EUR123\n" +
                        "CASH\n" +
                        "\n" +
                        "Test123")
                .thenExpect(new Payment.Builder()
                        .withIban("DE11720500000000061390")
                        .withAmount("123")
                        .withCurrency("EUR")
                        .withBic("AUGSDE77XXX")
                        .withDate(NOW)
                        .withName("Matthias Klass")
                        .withReason("Test123")
                        .build());
    }

    @Test
    @UseDataProvider("provider")
    public void should_parse_text(TestCase testCase) {
        DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
        given(dateTimeProvider.now()).willReturn(NOW);
        QrCodeTextParser parser = new QrCodeTextParser(dateTimeProvider);

        Payment result = parser.parse(testCase.text);

        assertThat(result).isEqualTo(testCase.expectedPayment);
    }

    private static class TestCase {
        String desc;
        String text;
        Payment expectedPayment;

        public TestCase(String desc) {
            this.desc = desc;
        }

        public TestCase withText(String text) {
            this.text = text;
            return this;
        }

        public TestCase thenExpect(Payment expectedPayment) {
            this.expectedPayment = expectedPayment;
            return this;
        }
    }
}