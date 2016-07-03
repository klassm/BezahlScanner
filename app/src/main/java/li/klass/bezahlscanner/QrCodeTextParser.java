package li.klass.bezahlscanner;

import android.support.annotation.NonNull;

import com.google.common.base.Charsets;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

public class QrCodeTextParser {

    public static final String SINGLEPAYMENT_PREFIX = "bank://singlepaymentsepa?";
    private final DateTimeProvider dateTimeProvider;

    public QrCodeTextParser(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    public Payment parse(String text) {

        text = StringUtils.trimToNull(text);
        if (text == null) {
            return null;
        }
        if (text.startsWith(SINGLEPAYMENT_PREFIX)) {
            return parseSinglePayments(text);
        } else if (text.startsWith("BCD")) {
            return parseGirocode(text);
        } else {
            return null;
        }
    }

    private Payment parseGirocode(String text) {
        String[] lines = text.split("[\r\n]");
        if (lines.length < 10) {
            return null;
        }

        // Field 'reason' after first empty line
        int indexEmptyLine = Arrays.asList(lines).indexOf("");

        return new Payment.Builder()
                .withDate(dateTimeProvider.now())
                .withBic(lines[4])
                .withName(lines[5])
                .withIban(lines[6])
                .withAmount(lines[7].replaceAll("[A-Za-z]+", ""))
                .withCurrency(lines[7].replaceAll("[^A-Za-z]+", ""))
                .withReason(lines[indexEmptyLine > 0 ? indexEmptyLine + 1 : 10])
                .build();
    }

    @NonNull
    private Payment parseSinglePayments(String text) {
        String charset = Charsets.UTF_8.displayName();
        try {
            text = URLDecoder.decode(text, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String[] parts = text.replace(SINGLEPAYMENT_PREFIX, "").split("&");

        Payment.Builder builder = new Payment.Builder()
                .withDate(dateTimeProvider.now());

        for (String part : parts) {
            String[] elements = part.split("=");
            String key = elements[0];
            String value = elements[1];
            switch (key) {
                case "name":
                    builder.withName(value);
                    break;
                case "reason":
                    builder.withReason(value);
                    break;
                case "iban":
                    builder.withIban(value);
                    break;
                case "bic":
                    builder.withBic(value);
                    break;
                case "amount":
                    builder.withAmount(value);
                    break;
            }
        }
        return builder.build();
    }
}
