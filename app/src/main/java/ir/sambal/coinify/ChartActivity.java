package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

import java.util.List;

import ir.sambal.coinify.network.CandleRequest;
import ir.sambal.coinify.network.CoinifyOkHttp;

public class ChartActivity extends AppCompatActivity {
    private int prevToggle;
    private CandleRequest candleRequest;


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
        setContentView(R.layout.activity_chart);

        candleRequest = new CandleRequest(CoinifyOkHttp.create(this));

        ToggleSwitch chartToggle = findViewById(R.id.chart_toggle);
        chartToggle.setCheckedPosition(0);
        prevToggle = 0;
        CandleChart.initialize(this);
        Coin c = new Coin(1, "Bitcoin", "BTC", 1, 2, 3, 4); //TODO: get coin from intent
        candleRequest.getCandles(this, c, CandleRequest.Range.weekly);
        CandleChart.draw(this, c, CandleRequest.Range.weekly);
        candleRequest.getCandles(this, c, CandleRequest.Range.oneMonth);

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
    }
}