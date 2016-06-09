package li.klass.bezahlscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

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
        setText(convertView, R.id.amount, formatAmount(item.getAmount()));
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
            sb.append(value.charAt(i-1));
            if (i % packageSize == 0) sb.append(packageSeparator);
        }
        return sb.toString();
    }

    private String formatAmount(String amount) {
        if (amount == null || amount.isEmpty()) amount = "0.00";
        return NumberFormat.getCurrencyInstance().format(Double.parseDouble(amount));
    }

    private void setText(View convertView, int id, String value) {
        ((TextView) convertView.findViewById(id)).setText(value);
    }
}
