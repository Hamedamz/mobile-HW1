package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.content.Intent;

import android.net.Network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ir.sambal.coinify.db.AppDatabase;
import ir.sambal.coinify.network.CoinRequest;
import ir.sambal.coinify.network.CoinifyOkHttp;
import ir.sambal.coinify.network.NetworkStatus;
import ir.sambal.coinify.repository.CoinRepository;
import ir.sambal.coinify.thread.ThreadPoolManager;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements DroidListener {

    private Handler mainThreadHandler  = HandlerCompat.createAsync(Looper.getMainLooper());
    private ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();;

    private ProgressBar progressBar;
    private AppDatabase db;
    private ListView listView;
    private CoinRepository coinRepository;

    public static final int COIN_LOAD_NO = 10;

    private final List<Coin> coins = new ArrayList<>();

    private DroidNet mDroidNet;
    public NetworkStatus networkStatus;

    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DroidNet.init(this);

        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.INVISIBLE);

        db = AppDatabase.getInstance(this);
        OkHttpClient coinMarketClient = CoinifyOkHttp.create(this);
        CoinRequest coinRequest = new CoinRequest(coinMarketClient);
        coinRepository = new CoinRepository(db.coinDao(), coinRequest, threadPoolManager, mainThreadHandler);

        listView = findViewById(R.id.coin_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.coin_card, R.id.coin_name, new String[]{});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            loadChartActivity(this.coins.get(position).getId());
        });

        loadMoreCoins();
        Button moreBtn = findViewById(R.id.more_btn);
        moreBtn.setOnClickListener(v -> loadMoreCoins());
        Button reloadBtn = findViewById(R.id.reload_btn);
        reloadBtn.setOnClickListener(v -> reloadCoins());

        mDroidNet = DroidNet.getInstance();
        mDroidNet.addInternetConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDroidNet.removeInternetConnectivityChangeListener(this);
    }

    private void loadChartActivity(int coinId) {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra(ChartActivity.COIN_ID, coinId);
        startActivity(intent);
    }

    private void reloadCoins() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            notYetToast();
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        progressBar.setVisibility(View.VISIBLE);

        coinRepository.loadFreshCoins(1, COIN_LOAD_NO, (coins, isFinalCall) -> {
            synchronized (MainActivity.this.coins) {
                MainActivity.this.coins.clear();
                updateCoins(coins);
                if (isFinalCall) {
//                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
//                    });
                }
            }
        });
    }

    public void notYetToast() {
        Context context = getApplicationContext();
        CharSequence text = "به کجا چنین شتابان...";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void noInternetToast() {
        Context context = getApplicationContext();
        CharSequence text = "No Network!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    public void updateCoins(Coin... newCoins) {
        synchronized (coins) {
            for (Coin coin : newCoins) {
                addOrUpdateCoin(coin);
            }

            Collections.sort(coins, (c1, c2) -> Double.compare(c2.getMarketCap(), c1.getMarketCap()));

//            runOnUiThread(() -> {
                final CoinAdapter adapter = new CoinAdapter(this, coins);
                listView.setAdapter(adapter);
                for (int i = 0; i < coins.size(); i++) {
                    Coin coin = coins.get(i);
                    int finalI = i;
                    coinRepository.fetchCoinImageURL(coin, (imageURL -> {
//                        runOnUiThread(() -> {
                            View childView = listView.getChildAt(finalI);
                            if (childView != null) {
                                listView.getAdapter().getView(finalI, childView, listView);
                            }
//                        });
                    }));
                }
//            });
        }
    }

    private void addOrUpdateCoin(Coin coin) {
        synchronized (coins) {
            // TODO: Can we simplify it with replace?!
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
        progressBar.setVisibility(View.VISIBLE);
        int index = this.coins.size() + 1;
        coinRepository.getCoins(index, COIN_LOAD_NO, networkStatus, (coins, isFinalCall) -> {
            synchronized (MainActivity.this.coins) {
                updateCoins(coins);
                if (isFinalCall) {
//                    runOnUiThread(() -> progressBar.setVisibility(View.INVISIBLE));
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        Button reloadBtn = findViewById(R.id.reload_btn);
        if (isConnected) {
            networkStatus = NetworkStatus.CONNECTED;
            reloadBtn.setEnabled(true);
        } else {
            networkStatus = NetworkStatus.NOT_CONNECTED;
            reloadBtn.setEnabled(false);
            noInternetToast();
        }
    }
}