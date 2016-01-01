package li.klass.bezahlscanner;

import org.joda.time.DateTime;

public class Payment {
    private String name;
    private String iban;
    private String bic;
    private String amount;
    private String reason;
    private DateTime date;


    private Payment(Builder builder) {
        name = builder.name;
        iban = builder.iban;
        bic = builder.bic;
        amount = builder.amount;
        reason = builder.reason;
        date = builder.date;
    }

    public String getName() {
        return name;
    }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }

    public String getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public DateTime getDate() {
        return date;
    }

    public static final class Builder {
        private String name;
        private String iban;
        private String bic;
        private String amount;
        private String reason;
        private DateTime date;

        public Builder() {
        }

        public Builder withName(String val) {
            name = val;
            return this;
        }

        public Builder withIban(String val) {
            iban = val;
            return this;
        }

        public Builder withBic(String val) {
            bic = val;
            return this;
        }

        public Builder withAmount(String val) {
            amount = val;
            return this;
        }

        public Builder withReason(String val) {
            reason = val;
            return this;
        }

        public Builder withDate(DateTime val) {
            date = val;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        if (name != null ? !name.equals(payment.name) : payment.name != null) return false;
        if (iban != null ? !iban.equals(payment.iban) : payment.iban != null) return false;
        if (bic != null ? !bic.equals(payment.bic) : payment.bic != null) return false;
        if (amount != null ? !amount.equals(payment.amount) : payment.amount != null) return false;
        if (reason != null ? !reason.equals(payment.reason) : payment.reason != null)
            return false;
        return !(date != null ? !date.equals(payment.date) : payment.date != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (iban != null ? iban.hashCode() : 0);
        result = 31 * result + (bic != null ? bic.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "name='" + name + '\'' +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                ", amount='" + amount + '\'' +
                ", reason='" + reason + '\'' +
                ", date=" + date +
                '}';
    }
}
