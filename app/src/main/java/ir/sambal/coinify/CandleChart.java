package ir.sambal.coinify;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;

public class CandleChart {

    public static void draw(MainActivity m, Coin coin, CandleRequest.Range range) {
        ArrayList<CandleEntry> candleValues= new ArrayList<>();
        while (coin.getCandles(range).size() == 0) {

        }
        ArrayList<Candle> candles = (ArrayList<Candle>) coin.getCandles(range);
        Log.v("JOONJOON", "6");
        Log.v("JOONJOON", String.valueOf(candles.size()));

        for (int i = 0; i < candles.size(); i++) {
            Candle c = candles.get(i);
            candleValues.add(new CandleEntry(i, (float) c.getPriceHigh(), (float) c.getPriceLow(), (float) c.getPriceOpen(), (float) c.getPriceClose()));
        }

        CandleDataSet set1 = new CandleDataSet(candleValues, "Chart");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.GRAY);
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.GREEN);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(false);

        CandleData data = new CandleData(set1);

        CandleStickChart candleStickChart = m.findViewById(R.id.candle_stick_chart);

        candleStickChart.setData(data);
        candleStickChart.invalidate();
    }

    public static void initialize(MainActivity m) {
        CandleStickChart candleStickChart = m.findViewById(R.id.candle_stick_chart);
        candleStickChart.setHighlightPerDragEnabled(true);

        candleStickChart.setDrawBorders(true);

        candleStickChart.setBorderColor(Color.GRAY);

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();

        xAxis.setDrawGridLines(false);// disable x axis grid lines
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candleStickChart.getLegend();
        l.setEnabled(false);
    }
}
