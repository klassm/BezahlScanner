package li.klass.bezahlscanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.payment.view.*
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class PaymentAdapter(context: Context) : ArrayAdapter<Payment>(context, LAYOUT) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(LAYOUT, null)

        val item = getItem(position)!!

        return view.apply {
            date.text = item.date.toString("YYYY-MM-dd HH:mm:ss")
            name.text = item.name
            iban.text = formatIBAN(item.iban)
            bic.text = formatBIC(item.bic)
            amount.text = formatAmount(item.amount, item.currency)
            reason.text = item.reason
        }
    }

    fun setData(payments: List<Payment>) {
        clear()

        addAll(payments)
        notifyDataSetChanged()
    }

    private fun formatIBAN(iban: String): String {
        return formatAsPackages(iban, 4, " ")
    }

    private fun formatBIC(bic: String): String {
        return formatAsPackages(bic, 4, " ")
    }

    private fun formatAsPackages(value: String, packageSize: Int, packageSeparator: String): String {
        val sb = StringBuilder()
        (1..value.length).forEach { i ->
            sb.append(value[i - 1])
            if (i % packageSize == 0) sb.append(packageSeparator)
        }
        return sb.toString()
    }

    private fun formatAmount(amount: String, currency: String): String {
        var formattedAmount = amount
        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        numberFormat.isGroupingUsed = true
        try {
            formattedAmount = numberFormat.format(parseAmount(formattedAmount))
        } catch (e: Exception) {
        }

        return getCurrencySymbol(currency) + " " + formattedAmount
    }

    @Throws(ParseException::class)
    private fun parseAmount(amount: String?): Double {
        if (amount == null || amount.isEmpty()) return 0.0
        try {
            return NumberFormat.getInstance().parse(amount).toDouble()
        } catch (e: ParseException) {
            return NumberFormat.getInstance(Locale.US).parse(amount).toDouble()
        }
    }

    private fun getCurrencySymbol(currency: String?): String {
        if (currency == null || currency.isEmpty()) return "€"
        try {
            return Currency.getInstance(currency).symbol
        } catch (e: Exception) {
            return currency
        }

    }

    companion object {
        val LAYOUT = R.layout.payment
    }
}
