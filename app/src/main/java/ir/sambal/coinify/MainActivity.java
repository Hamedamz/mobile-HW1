package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import android.view.View;

import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;

import java.util.ArrayList;

import ir.sambal.coinify.db.AppDatabase;
import ir.sambal.coinify.network.CoinRequest;
import ir.sambal.coinify.network.CoinifyOkHttp;
import ir.sambal.coinify.network.NetworkStatus;
import ir.sambal.coinify.repository.CoinRepository;
import ir.sambal.coinify.repository.GlobalRepositoryErrors;
import ir.sambal.coinify.thread.ThreadPoolManager;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements DroidListener {

    private Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    private ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();

    private ProgressBar progressBar;
    private AppDatabase db;
    private ListView listView;
    private CoinRepository coinRepository;

    public static final int COIN_LOAD_NO = 10;

    private CoinAdapter coins;

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
        coins = new CoinAdapter(this, new ArrayList<>());
        listView.setAdapter(coins);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            loadChartActivity(this.coins.getItem(position).getId());
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

        coinRepository.loadFreshCoins(1, COIN_LOAD_NO, new CoinRepository.CoinsResponseCallback() {
            @Override
            public void success(Coin[] coins, boolean isFinalCall) {
                synchronized (MainActivity.this.coins) {
                    MainActivity.this.coins.clear();
                    updateCoins(coins);
                    if (isFinalCall) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void error(int errorNumber) {
                GlobalRepositoryErrors.handleGlobalError(getApplicationContext(), errorNumber);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void notYetToast() {
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.please_wait);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void noInternetToast() {
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.no_network);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    public void updateCoins(Coin... newCoins) {
        synchronized (coins) {
            for (Coin coin : newCoins) {
                addOrUpdateCoin(coin);
            }

            coins.sort((c1, c2) -> Double.compare(c2.getMarketCap(), c1.getMarketCap()));

            for (int i = 0; i < coins.getCount(); i++) {
                Coin coin = coins.getItem(i);
                int finalI = i;
                coinRepository.fetchCoinImageURL(coin, (imageURL -> {
                    View childView = listView.getChildAt(finalI);
                    if (childView != null) {
                        listView.getAdapter().getView(finalI, childView, listView);
                    }
                }));
            }
        }
    }

    private void addOrUpdateCoin(Coin coin) {
        synchronized (coins) {
            // TODO: Can we simplify it with replace?!
            for (int i = 0; i < coins.getCount(); i++) {
                if (coins.getItem(i).getId() == coin.getId()) {
                    if (coins.getItem(i).getLastUpdated().before(coin.getLastUpdated())) {
                        // use old image is coin has image TODO: it should not be here :-\
                        if (coins.getItem(i).getImageURL() == null && coin.getImageURL() != null) {
                            coin.setImageURL(coins.getItem(i).getImageURL());
                        }
                        coins.remove(coins.getItem(i));
                        coins.insert(coin, i);
                        return;
                    }
                }
            }
            coins.add(coin);

        }
    }

    public void loadMoreCoins() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            notYetToast();
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        progressBar.setVisibility(View.VISIBLE);
        int index = this.coins.getCount() + 1;
        coinRepository.getCoins(index, COIN_LOAD_NO, new CoinRepository.CoinsResponseCallback() {
            @Override
            public void success(Coin[] coins, boolean isFinalCall) {
                synchronized (MainActivity.this.coins) {
                    updateCoins(coins);
                    if (isFinalCall) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void error(int errorNumber) {
                GlobalRepositoryErrors.handleGlobalError(getApplicationContext(), errorNumber);
                progressBar.setVisibility(View.INVISIBLE);
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