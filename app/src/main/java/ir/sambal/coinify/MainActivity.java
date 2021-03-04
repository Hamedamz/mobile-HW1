package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private String[] listItem;

    public static final int COIN_LOAD_NO = 10;

    private List<Coin> coins = new ArrayList<>();

    public void addCoins(List<Coin> coins) {
        for (int i = 0; i < COIN_LOAD_NO; i++) {
            Coin c = coins.get(i);
            CoinRequest.requestCoinImage(c);
        }
        this.coins.addAll(coins);
    }

    public void addCandles(Coin coin, List<Candle> candles, CandleRequest.Range range) {
        switch (range) {
            case weekly:
                coin.setWeekCandles(candles);
                break;
            case oneMonth:
                coin.setMonthCandles(candles);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadMoreCoins();
        Button moreBtn = findViewById(R.id.more_btn);
        moreBtn.setOnClickListener(v -> loadMoreCoins());
        Button reloadBtn = findViewById(R.id.reload_btn);
        reloadBtn.setOnClickListener(v -> reloadCoins());

        Thread thread = new Thread(() -> {
            Log.i("ME", "Shoro shod!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                Log.i("ME", "Update kon");
                listItem = new String[]{"Arshia"}; // getResources().getStringArray(R.array.array_technology);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.mylist, R.id.textView, listItem);
                listView.setAdapter(adapter);
                Log.i("ME", "Update shod!");
            });
        });

        thread.start();

        listView = findViewById(R.id.listView);
        listItem = new String[]{"Hamed"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.mylist, R.id.textView, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            // TODO Auto-generated method stub
            String value = adapter.getItem(position);
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();

        });

//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private void reloadCoins() {
        coins.clear();
        loadMoreCoins();
    }

    public synchronized void loadMoreCoins() {
        int index = this.coins.size() + 1;
        CoinRequest.requestCoinData(this, index, COIN_LOAD_NO);
    }
}