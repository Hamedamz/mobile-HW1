package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

import java.util.Arrays;

import ir.sambal.coinify.db.AppDatabase;
import ir.sambal.coinify.network.CandleRequest;
import ir.sambal.coinify.network.CoinRequest;
import ir.sambal.coinify.network.CoinifyOkHttp;
import ir.sambal.coinify.repository.CandleRepository;
import ir.sambal.coinify.repository.CoinRepository;
import ir.sambal.coinify.repository.GlobalRepositoryErrors;
import ir.sambal.coinify.thread.ThreadPoolManager;
import okhttp3.OkHttpClient;

public class ChartActivity extends AppCompatActivity {
    private final Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    private final ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();

    private int coinId;
    private Coin coin = null;

    public static final String COIN_ID = "COIN_ID";
    private int prevToggle;
    private ProgressBar progressBar;
    private AppDatabase db;
    private CoinRepository coinRepository;
    private CandleRepository candleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        progressBar = findViewById(R.id.indeterminateBar);
        db = AppDatabase.getInstance(this);
        OkHttpClient coinMarketClient = CoinifyOkHttp.create(this);

        CoinRequest coinRequest = new CoinRequest(coinMarketClient);
        coinRepository = new CoinRepository(db.coinDao(), coinRequest, threadPoolManager, mainThreadHandler);

        coinId = getIntent().getIntExtra(ChartActivity.COIN_ID, -1);

        CandleRequest candleRequest = new CandleRequest(CoinifyOkHttp.create(this));
        candleRepository = new CandleRepository(db.candleDao(), candleRequest, threadPoolManager, mainThreadHandler);

        ToggleSwitch chartToggle = findViewById(R.id.chart_toggle);
        chartToggle.setCheckedPosition(0);
        prevToggle = 0;
        CandleChart.initialize(this);

        loadCoin();

        chartToggle.setOnChangeListener(position -> {
            if (prevToggle == position || position == -1 || coin == null) {
                return;
            }
            switch (position) {
                case 0:
                    prevToggle = 0;
                    CandleChart.draw(ChartActivity.this, coin, CandleRequest.Range.weekly);
                    break;
                case 1:
                    prevToggle = 1;
                    CandleChart.draw(ChartActivity.this, coin, CandleRequest.Range.oneMonth);
                    break;
            }
        });

    }

    private void loadCoin() {
        coinRepository.getCoin(coinId, new CoinRepository.CoinResponseCallback() {
            @Override
            public void success(Coin c) {
                coin = c;
                progressBar.setVisibility(View.INVISIBLE);
                fetchCoinCandles();
            }

            @Override
            public void error(int errorNumber) {
                if (!GlobalRepositoryErrors.handleGlobalError(getApplicationContext(), errorNumber)) {
                    if (errorNumber == CoinRepository.CoinResponseCallback.COIN_NOT_FOUND) {
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.coin_not_found), duration);
                        toast.show();
                        ChartActivity.this.finish();
                        return;
                    }
                }
                // retry!
                ChartActivity.this.loadCoin();

            }
        });
    }

    private void fetchCoinCandles() {
        progressBar.setVisibility(View.VISIBLE);
        candleRepository.getCandles(coin, new CandleRepository.CandlesResponseCallback() {
            @Override
            public void setCandles(Candle[] candles, boolean isFinalCall) {
                synchronized (ChartActivity.this) {
                    coin.setCandles(Arrays.asList(candles));
                }
                CandleChart.draw(ChartActivity.this, coin, CandleRequest.Range.weekly);
                if (isFinalCall) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void error(int errorNumber) {
                if (!GlobalRepositoryErrors.handleGlobalError(getApplicationContext(), errorNumber)) {
                    if (errorNumber == CandleRepository.CandlesResponseCallback.NO_CANDLES_FOUND) {
                        ((CandleStickChart) findViewById(R.id.candle_stick_chart)).setNoDataText(getResources().getString(R.string.chart_no_data));
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}