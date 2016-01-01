package li.klass.bezahlscanner;

import com.google.common.base.Charsets;
import com.google.common.collect.ComparisonChain;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

public class DataHolder {
    public static final Comparator<Payment> PAYMENT_COMPARATOR = new Comparator<Payment>() {
        @Override
        public int compare(Payment z1, Payment z2) {
            return ComparisonChain.start()
                    .compare(z1.getDate(), z2.getDate())
                    .compare(z1.getName(), z2.getName())
                    .compare(z1.getIban(), z2.getIban())
                    .result();
        }
    };
    public static final String DATE = "date";
    public static final String NAME = "name";
    public static final String BIC = "bic";
    public static final String IBAN = "iban";
    public static final String AMOUNT = "amount";
    public static final String REASON = "reason";
    public static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader(DATE, NAME, BIC, IBAN, AMOUNT, REASON);

    private List<Payment> payments = new ArrayList<>();

    public DataHolder(String content) {
        try {
            List<Payment> payments = new ArrayList<>();
            List<CSVRecord> records = from(CSVParser.parse(content, CSV_FORMAT)).skip(1).toList();
            for (CSVRecord record : records) {
                payments.add(new Payment.Builder()
                        .withName(record.get(NAME))
                        .withAmount(record.get(AMOUNT))
                        .withBic(record.get(BIC))
                        .withIban(record.get(IBAN))
                        .withReason(record.get(REASON))
                        .withDate(DateTime.parse(record.get(DATE)))
                        .build());
            }
            Collections.sort(payments, PAYMENT_COMPARATOR);
            this.payments = payments;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Payment> getPayments() {
        if (payments.size() < 10) {
            return payments;
        } else {
            return payments.subList(payments.size() - 10, payments.size());
        }
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    public String toCsv() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);){
            CSVPrinter csvPrinter = new CSVPrinter(outputStreamWriter, CSV_FORMAT);
            for (Payment payment : getPayments()) {
                csvPrinter.printRecord(payment.getDate().toString(), payment.getName(), payment.getBic(), payment.getIban(), payment.getAmount(), payment.getReason());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(os.toByteArray(), Charsets.UTF_8);
    }
}
