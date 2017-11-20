package li.klass.bezahl.scanner

import com.google.common.base.Charsets
import com.google.common.collect.ComparisonChain
import com.google.common.collect.FluentIterable.from
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.joda.time.DateTime
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*

class DataHolder(content: String) {
    private var payments: MutableList<Payment> = ArrayList()

    init {
        try {
            val records = from(CSVParser.parse(content, CSV_FORMAT)).skip(1).toList()

            payments = records.map {
                Payment(
                        name = it.get(NAME),
                        amount = it.get(AMOUNT),
                        bic = it.get(BIC),
                        iban = it.get(IBAN),
                        reason = it.get(REASON),
                        date = DateTime.parse(it.get(DATE))
                )
            }
                    .sortedWith(PAYMENT_COMPARATOR)
                    .toMutableList()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    fun getPayments(): List<Payment> {
        return if (payments.size < 10) {
            payments
        } else {
            payments.subList(payments.size - 10, payments.size)
        }
    }

    fun addPayment(payment: Payment) {
        payments.add(payment)
    }

    fun toCsv(): String {
        val os = ByteArrayOutputStream()

        try {
            OutputStreamWriter(os).use { outputStreamWriter ->
                val csvPrinter = CSVPrinter(outputStreamWriter, CSV_FORMAT)
                for (payment in getPayments()) {
                    csvPrinter.printRecord(payment.date.toString(), payment.name, payment.bic, payment.iban, payment.amount, payment.reason)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        return String(os.toByteArray(), Charsets.UTF_8)
    }

    companion object {
        val PAYMENT_COMPARATOR: Comparator<Payment> = Comparator { z1, z2 ->
            ComparisonChain.start()
                    .compare(z1.date, z2.date)
                    .compare(z1.name, z2.name)
                    .compare(z1.iban, z2.iban)
                    .result()
        }
        val DATE = "date"
        val NAME = "name"
        val BIC = "bic"
        val IBAN = "iban"
        val AMOUNT = "amount"
        val REASON = "reason"
        val CSV_FORMAT = CSVFormat.DEFAULT.withHeader(DATE, NAME, BIC, IBAN, AMOUNT, REASON)
    }
}
