package ir.sambal.coinify;

import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;
import java.util.List;

import ir.sambal.coinify.network.CandleRequest;

public class CandleChart {

    public static void draw(ChartActivity m, Coin coin, CandleRequest.Range range) {
        ArrayList<CandleEntry> candleValues = new ArrayList<>();
        if (coin.getCandles(range).size() == 0) {
            return;
        }
        List<Candle> candles = coin.getCandles(range);

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

    public static void initialize(ChartActivity m) {
        CandleStickChart candleStickChart = m.findViewById(R.id.candle_stick_chart);
        candleStickChart.setHighlightPerDragEnabled(true);
        candleStickChart.setNoDataText(m.getResources().getString(R.string.chart_loading));

        candleStickChart.setDrawBorders(true);

        candleStickChart.setBorderColor(Color.GRAY);

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawGridLines(false);// disable x axis grid lines
        rightAxis.setDrawLabels(true);
        yAxis.setDrawLabels(false);
        yAxis.setDrawLabels(false);

        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candleStickChart.getLegend();
        l.setEnabled(false);

        candleStickChart.getDescription().setEnabled(false);
    }
}
