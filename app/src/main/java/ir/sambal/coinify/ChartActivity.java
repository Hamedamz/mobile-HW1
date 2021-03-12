package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

import java.util.Arrays;

import ir.sambal.coinify.db.AppDatabase;
import ir.sambal.coinify.network.CandleRequest;
import ir.sambal.coinify.network.CoinRequest;
import ir.sambal.coinify.network.CoinifyOkHttp;
import ir.sambal.coinify.repository.CandleRepository;
import ir.sambal.coinify.repository.CoinRepository;
import ir.sambal.coinify.thread.ThreadPoolManager;
import okhttp3.OkHttpClient;

public class ChartActivity extends AppCompatActivity {
    private final Handler mainThreadHandler  = HandlerCompat.createAsync(Looper.getMainLooper());
    private final ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();;

    public static final String COIN_ID = "COIN_ID";
    private int prevToggle;
    private ProgressBar progressBar;
    private CoinRepository coinRepository;
    private CandleRepository candleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        progressBar = findViewById(R.id.indeterminateBar);
        AppDatabase db = AppDatabase.getInstance(this);
        OkHttpClient coinMarketClient = CoinifyOkHttp.create(this);
        CoinRequest coinRequest = new CoinRequest(coinMarketClient);
        coinRepository = new CoinRepository(db.coinDao(), coinRequest, threadPoolManager, mainThreadHandler);

        int coinId = getIntent().getIntExtra(ChartActivity.COIN_ID, -1);

        CandleRequest candleRequest = new CandleRequest(CoinifyOkHttp.create(this));

        candleRepository = new CandleRepository(db.candleDao(), candleRequest, threadPoolManager, mainThreadHandler);

        ToggleSwitch chartToggle = findViewById(R.id.chart_toggle);
        chartToggle.setCheckedPosition(0);
        prevToggle = 0;
        CandleChart.initialize(this);

        coinRepository.getCoin(coinId, (c) -> {
            candleRepository.getCandles(c, (candles, isFinalCall) -> {
                synchronized (ChartActivity.this) {
                    c.setCandles(Arrays.asList(candles));
                }
                runOnUiThread(() -> {
                    CandleChart.draw(this, c, CandleRequest.Range.weekly);
                    if (isFinalCall) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            });

            chartToggle.setOnChangeListener(position -> {
                if (prevToggle == position || position == -1) {
                    return;
                }
                switch (position) {
                    case 0:
                        prevToggle = 0;
                        CandleChart.draw(ChartActivity.this, c, CandleRequest.Range.weekly);
                        break;
                    case 1:
                        prevToggle = 1;
                        CandleChart.draw(ChartActivity.this, c, CandleRequest.Range.oneMonth);
                        break;
                }
            });
        });
    }
}