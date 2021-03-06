package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

import java.util.Arrays;

import ir.sambal.coinify.db.AppDatabase;
import ir.sambal.coinify.network.CandleRequest;
import ir.sambal.coinify.network.CoinRequest;
import ir.sambal.coinify.network.CoinifyOkHttp;
import ir.sambal.coinify.repository.CandleRepository;
import ir.sambal.coinify.repository.CoinRepository;
import okhttp3.OkHttpClient;

public class ChartActivity extends AppCompatActivity {
    public static final String COIN_ID = "COIN_ID";
    private int prevToggle;
    private CoinRepository coinRepository;
    private CandleRepository candleRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        AppDatabase db = AppDatabase.getInstance(this);
        OkHttpClient coinMarketClient = CoinifyOkHttp.create(this);
        CoinRequest coinRequest = new CoinRequest(coinMarketClient);
        coinRepository = new CoinRepository(db.coinDao(), coinRequest);

        int coinId = getIntent().getIntExtra(ChartActivity.COIN_ID, -1);

        CandleRequest candleRequest = new CandleRequest(CoinifyOkHttp.create(this));

        candleRepository = new CandleRepository(db.candleDao(), candleRequest);

        ToggleSwitch chartToggle = findViewById(R.id.chart_toggle);
        chartToggle.setCheckedPosition(0);
        prevToggle = 0;
        CandleChart.initialize(this);

        coinRepository.getCoin(coinId, (c) -> {
            candleRepository.getCandles(c, (candles) -> {
                c.setCandles(Arrays.asList(candles));
                runOnUiThread(() -> {
                    CandleChart.draw(this, c, CandleRequest.Range.weekly);
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