package ir.sambal.coinify.network;


import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import ir.sambal.coinify.Candle;
import ir.sambal.coinify.Coin;
import ir.sambal.coinify.TimestampUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CandleRequest {

    private OkHttpClient client;

    public CandleRequest(OkHttpClient client) {
        this.client = client;
    }

    public enum Range {
        weekly,
        oneMonth,
    }

    public void getCandles(Coin coin, CandlesLoadedCallback callback) {
        String date = TimestampUtils.getISO8601StringForCurrentDate();

        String miniUrl = "period_id=1DAY".concat("&time_end=".concat(date).concat("&limit=30"));

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.coinapi.io/v1/ohlcv/".concat(coin.getSymbol()).concat("/USD/history?".concat(miniUrl)))
                .newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.error();
                Log.v("OKHTTP", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String myResponse = Objects.requireNonNull(response.body()).string();

                try {
                    JSONArray json = new JSONArray(myResponse);
                    Candle[] candles = new Candle[json.length()];
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject c = json.getJSONObject(i);
                        Date startDate = TimestampUtils.getDateForISO8601String(c.getString("time_period_start"));
                        double priceHigh = c.getDouble("price_high");
                        double priceLow = c.getDouble("price_low");
                        double priceOpen = c.getDouble("price_open");
                        double priceClose = c.getDouble("price_close");
                        candles[i] = new Candle(startDate, priceHigh, priceLow, priceOpen, priceClose);
                    }
                    callback.success(candles);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.error();
                }
            }
        });
    }

    public interface CandlesLoadedCallback {
        void success(Candle... candles);
        default void error() {}
    }
}
