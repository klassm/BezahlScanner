package li.klass.bezahlscanner

import org.joda.time.DateTime

class Payment(val name: String,
              val iban: String,
              val bic: String,
              val amount: String,
              val currency: String = "EUR",
              val reason: String,
              val date: DateTime
) {
    override fun toString(): String {
        return "Payment{" +
                "name='" + name + '\'' +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                ", amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", reason='" + reason + '\'' +
                ", date=" + date +
                '}'
    }
}
