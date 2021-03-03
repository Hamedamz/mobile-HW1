package ir.sambal.coinify;


import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CandleRequest {

    private CandleRequest() {
    }

    public enum Range {
        weekly,
        oneMonth,
    }

    public static void getCandles(Coin coin, Range range) {

        OkHttpClient okHttpClient = new OkHttpClient();

        String date = TimestampUtils.getISO8601StringForCurrentDate();

        String miniUrl;
        switch (range) {
            case weekly:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(date).concat("&limit=7"));
                break;
            case oneMonth:
                miniUrl = "period_id=1DAY".concat("&time_end=".concat(date).concat("&limit=30"));
                break;
            default:
                miniUrl = "";
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(coin.getSymbol()).concat("/USD/history?".concat(miniUrl)))
                .newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url)
                .addHeader("X-CoinAPI-Key", "968BAAAE-173B-47B1-A5DB-29151BE1D2EC")
                .build();

        List<Candle> candles = new ArrayList<>();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.v("TAG", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String myResponse = Objects.requireNonNull(response.body()).string();

                try {
                    JSONArray json = new JSONArray(myResponse);
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject c = json.getJSONObject(i);
                        double priceHigh = c.getDouble("price_high");
                        double priceLow = c.getDouble("price_low");
                        double priceOpen = c.getDouble("price_open");
                        double priceClose = c.getDouble("price_close");
                        candles.add(new Candle(priceHigh, priceLow, priceOpen, priceClose));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Log.v("JOONJOON", String.valueOf(candles));

        switch (range) {
            case weekly:
                coin.setWeekCandles(candles);
                break;
            case oneMonth:
                coin.setMonthCandles(candles);
                break;
        }
    }
}
