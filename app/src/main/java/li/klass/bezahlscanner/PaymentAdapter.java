package li.klass.bezahlscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        setText(convertView, R.id.iban, item.getIban());
        setText(convertView, R.id.bic, item.getBic());
        setText(convertView, R.id.amount, item.getAmount());
        setText(convertView, R.id.reason, item.getReason());

        return convertView;
    }

    private void setText(View convertView, int id, String value) {
        ((TextView) convertView.findViewById(id)).setText(value);
    }

    public void setData(List<Payment> payments) {
        clear();
        for (Payment payment : payments) {
            insert(payment, 0);
        }
        notifyDataSetChanged();
    }
}
