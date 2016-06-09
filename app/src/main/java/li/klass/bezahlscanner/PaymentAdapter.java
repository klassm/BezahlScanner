package li.klass.bezahlscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends ArrayAdapter<Payment> {

    public static final int LAYOUT = R.layout.payment;

    public PaymentAdapter(Context context) {
        super(context, LAYOUT);
    }

    public PaymentAdapter(Context context, List<Payment> items) {
        super(context, LAYOUT, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(LAYOUT, null);
        }

        Payment item = getItem(position);
        setText(convertView, R.id.date, item.getDate().toString("YYYY-MM-dd HH:mm:ss"));
        setText(convertView, R.id.name, item.getName());
        setText(convertView, R.id.iban, formatIBAN(item.getIban()));
        setText(convertView, R.id.bic, formatBIC(item.getBic()));
        setText(convertView, R.id.amount, formatAmount(item.getAmount(), item.getCurrency()));
        setText(convertView, R.id.reason, item.getReason());

        return convertView;
    }

    public void setData(List<Payment> payments) {
        clear();
        for (Payment payment : payments) {
            insert(payment, 0);
        }
        notifyDataSetChanged();
    }

    private String formatIBAN(String iban) {
        return formatAsPackages(iban, 4, " ");
    }

    private String formatBIC(String bic) {
        return formatAsPackages(bic, 4, " ");
    }

    private String formatAsPackages(String value, int packageSize, String packageSeparator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= value.length(); i++) {
            sb.append(value.charAt(i - 1));
            if (i % packageSize == 0) sb.append(packageSeparator);
        }
        return sb.toString();
    }

    private String formatAmount(String amount, String currency) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(true);
        try {
            amount = nf.format(parseAmount(amount));
        } catch (Exception e) {}
        return getCurrencySymbol(currency) + " " + amount;
    }

    private double parseAmount(String amount) throws ParseException {
        if (amount == null || amount.isEmpty()) return 0.0;
        try {
            return NumberFormat.getInstance().parse(amount).doubleValue();
        } catch (ParseException e) {
            return NumberFormat.getInstance(Locale.US).parse(amount).doubleValue();
        }
    }

    private String getCurrencySymbol(String currency) {
        if (currency == null || currency.isEmpty()) return "€";
        try {
            return Currency.getInstance(currency).getSymbol();
        } catch (Exception e) {
            return currency;
        }
    }

    private void setText(View convertView, int id, String value) {
        ((TextView) convertView.findViewById(id)).setText(value);
    }
}
