package ir.sambal.coinify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CoinAdapter extends ArrayAdapter<Coin> {
    public final static String TAG = "CoinAdapter";
    private final Context context;
    private final LayoutInflater inflater;

    private final List<Coin> coins;

    public CoinAdapter(Context context, List<Coin> coins) {
        super(context, R.layout.mylist, coins);

        this.context = context;
        this.coins = coins;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.mylist, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.textView)).setText(coins.get(position).getName());
        Log.d(TAG, coins.get(position).getName() + " ===> " + coins.get(position).getImageURL());
        if (coins.get(position).getImageURL() != null) {
            Glide.with(context).load(coins.get(position).getImageURL()).
                    into((ImageView) convertView.findViewById(R.id.imageView));
        }

        return convertView;
    }
}