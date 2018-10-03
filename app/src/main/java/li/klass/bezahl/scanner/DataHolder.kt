package li.klass.bezahl.scanner

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
            val records = CSVParser.parse(content, CSV_FORMAT).drop(1).toList()

            payments = records.asSequence().map {
                Payment(
                        name = it.get(NAME),
                        amount = it.get(AMOUNT),
                        bic = it.get(BIC),
                        iban = it.get(IBAN),
                        reason = it.get(REASON),
                        date = DateTime.parse(it.get(DATE))
                )
            }
                    .sortedWith(compareBy({ it.date }, { it.name }, { it.iban }))
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
        private const val DATE = "date"
        private const val NAME = "name"
        private const val BIC = "bic"
        private const val IBAN = "iban"
        private const val AMOUNT = "amount"
        private const val REASON = "reason"

        @JvmStatic
        private val CSV_FORMAT = CSVFormat.DEFAULT.withHeader(DATE, NAME, BIC, IBAN, AMOUNT, REASON)
    }
}
