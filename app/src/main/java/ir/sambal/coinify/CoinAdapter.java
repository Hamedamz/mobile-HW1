package ir.sambal.coinify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.List;

public class CoinAdapter extends ArrayAdapter<Coin> {
    public final static String TAG = "CoinAdapter";
    private final LayoutInflater inflater;

    public CoinAdapter(Context context, List<Coin> coins) {
        super(context, R.layout.coin_card, coins);

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.coin_card, parent, false);
        }
        Coin coin = getItem(position);

        ((TextView) convertView.findViewById(R.id.coin_name)).setText(coin.getName());
        ((TextView) convertView.findViewById(R.id.coin_symbol_name)).setText(coin.getSymbol());
        ((TextView) convertView.findViewById(R.id.coin_price)).
                setText(coin.getPrice() + " $");
        setTextWithColor(convertView, R.id.coin_price_change_1h_value, coin.getPercentChange1h());
        setTextWithColor(convertView, R.id.coin_price_change_1d_value, coin.getPercentChange24h());
        setTextWithColor(convertView, R.id.coin_price_change_1w_value, coin.getPercentChange7d());
        Log.d(TAG, coin.getName() + " ===> " + coin.getImageURL());
        Context context = getContext();
        if (coin.getImageURL() != null && context != null) {
            Glide.with(context).load(coin.getImageURL()).
                    into((ImageView) convertView.findViewById(R.id.coin_image));
        }

        return convertView;
    }

    private void setTextWithColor(View convertView, int viewId, int percentChange) {
        TextView textView = convertView.findViewById(viewId);
        textView.setText(percentChange + "%");
        if (percentChange == 0) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.price_nochange));
        } else if (percentChange > 0) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.price_increase));
        } else if (percentChange < 0) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.price_decrease));
        }
    }
}