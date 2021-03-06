package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

    }

    private void loadChartActivity(int coinId) {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra(ChartActivity.COIN_ID, coinId);
        startActivity(intent);
    }

    private void reloadCoins() {
        coinRepository.getCoins(1, COIN_LOAD_NO, (coins) -> {
            synchronized (MainActivity.this.coins) {
                MainActivity.this.coins.clear();
                updateCoins(coins);
            }
        });
    }

    public void updateCoins(Coin... newCoins) {
        synchronized (coins) {
            for (Coin coin : newCoins) {
                addOrUpdateCoin(coin);
            }

            Collections.sort(coins, (c1, c2) -> Double.compare(c2.getMarketCap(), c1.getMarketCap()));

            for (Coin coin : coins) {
                coinRepository.fetchCoinImageURL(coin, (imageURL -> {
                    // pass
                }));
            }

            runOnUiThread(() -> {
                final CoinAdapter adapter = new CoinAdapter(this, coins);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((adapterView, view, position, l) -> {
                    loadChartActivity(this.coins.get(position).getId());
                });
            });
        }
    }

    private void addOrUpdateCoin(Coin coin) {
        synchronized (coins) {
            for (int i = 0; i < coins.size(); i++) {
                if (coins.get(i).getId() == coin.getId()) {
                    if (coins.get(i).getLastUpdated().before(coin.getLastUpdated())) {
                        // use old image is coin has image TODO: it should not be here :-\
                        if (coins.get(i).getImageURL() == null && coin.getImageURL() != null) {
                            coin.setImageURL(coins.get(i).getImageURL());
                        }
                        coins.set(i, coin);
                        return;
                    }
                }
            }
            coins.add(coin);
        }
    }

    public void loadMoreCoins() {
        int index = this.coins.size() + 1;
        coinRepository.getCoins(index, COIN_LOAD_NO, (coins) -> {
            synchronized (MainActivity.this.coins) {
                updateCoins(coins);
            }
        });
    }
}