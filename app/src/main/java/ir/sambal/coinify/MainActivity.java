package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ir.sambal.coinify.db.AppDatabase;
import ir.sambal.coinify.network.CoinRequest;
import ir.sambal.coinify.network.CoinifyOkHttp;
import ir.sambal.coinify.repository.CoinRepository;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private ListView listView;
    private CoinRepository coinRepository;

    public static final int COIN_LOAD_NO = 10;

    private final List<Coin> coins = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        OkHttpClient coinMarketClient = CoinifyOkHttp.create(this);
        CoinRequest coinRequest = new CoinRequest(coinMarketClient);
        coinRepository = new CoinRepository(db.coinDao(), coinRequest);

        listView = findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.mylist, R.id.textView, new String[]{});
        listView.setAdapter(adapter);

        loadMoreCoins();
        Button moreBtn = findViewById(R.id.more_btn);
        moreBtn.setOnClickListener(v -> loadMoreCoins());
        Button reloadBtn = findViewById(R.id.reload_btn);
        reloadBtn.setOnClickListener(v -> reloadCoins());
        Button chartBtn = findViewById(R.id.tmp_chart);
        chartBtn.setOnClickListener(v -> loadChartActivity());

    }

    private void loadChartActivity() { //TODO: pass coin on click
        Intent intent = new Intent(this, ChartActivity.class);
        //intent.putExtra("COIN", ); TODO: place coin here
        startActivity(intent);
    }

    private void reloadCoins() {
        synchronized (coins) {
            coins.clear();
            loadMoreCoins();
        }
    }

    public void updateCoins() {
        synchronized (coins) {
            runOnUiThread(() -> {
                String[] listItem = new String[coins.size()];
                for (int i = 0; i < listItem.length; i++) {
                    listItem[i] = coins.get(i).getName();
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.mylist, R.id.textView, listItem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((adapterView, view, position, l) -> {
                    String value = adapter.getItem(position);
                    Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    private void addOrUpdateCoin(Coin coin) {
        synchronized (coins) {
            for (int i = 0; i < coins.size(); i++) {
                if (coins.get(i).getId() == coin.getId()) {
                    if (coins.get(i).getLastUpdated().before(coin.getLastUpdated())) {
                        coins.set(i, coin);
                        return;
                    }
                }
            }
            coins.add(coin);
        }
    }

    private void sortCoins() {
        synchronized (coins) {
            Collections.sort(coins, (c1, c2) -> c1.getLastUpdated().compareTo(c2.getLastUpdated()));
        }
    }

    public synchronized void loadMoreCoins() {
        int index = this.coins.size() + 1;
        coinRepository.getCoins(index, COIN_LOAD_NO, (coins) -> {
            synchronized (MainActivity.this.coins) {
                for (Coin c : coins) {
                    CoinRequest.requestCoinImage(c);
                    MainActivity.this.addOrUpdateCoin(c);
                }
                sortCoins();
                updateCoins();
            }
        });
    }
}