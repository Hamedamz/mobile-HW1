package ir.sambal.coinify;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CoinRequest {

    public List<Coin> requestCoinData(int start, int range) {

        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"))
                .newBuilder();
        urlBuilder.addQueryParameter("start", String.valueOf(start));
        urlBuilder.addQueryParameter("limit", String.valueOf(range));
        urlBuilder.addQueryParameter("convert", "USD");

        String url = urlBuilder.build().toString();


        final Request request = new Request.Builder().url(url)
                .addHeader("X-CMC_PRO_API_KEY", "4bf0fd5a-5704-4adc-a078-a0f480b17b86")
                .build();

        List<Coin> coins = new ArrayList<>();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String myResponse = Objects.requireNonNull(response.body()).string();

                try {
                    JSONObject json = new JSONObject(myResponse);
                    JSONArray data = json.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String name = c.getString("name");
                        String symbol = c.getString("symbol");
                        JSONObject quote = c.getJSONObject("quote");
                        JSONObject usd = quote.getJSONObject("USD");
                        int price = (int) Double.parseDouble(usd.getString("price"));
                        int percent_change_1h = (int) Double.parseDouble(usd.getString("percent_change_1h"));
                        int percent_change_24h = (int) Double.parseDouble(usd.getString("percent_change_24h"));
                        int percent_change_7d = (int) Double.parseDouble(usd.getString("percent_change_7d"));
                        coins.add(new Coin(name, symbol, price, percent_change_1h, percent_change_24h, percent_change_7d));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return coins;
    }

    public void requestCoinImage(Coin coin) {
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://pro-api.coinmarketcap.com/v1/cryptocurrency/info"))
                .newBuilder();
        urlBuilder.addQueryParameter("symbol", coin.getSymbol());
        urlBuilder.addQueryParameter("aux", "logo");

        String url = urlBuilder.build().toString();


        final Request request = new Request.Builder().url(url)
                .addHeader("X-CMC_PRO_API_KEY", "4bf0fd5a-5704-4adc-a078-a0f480b17b86")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String myResponse = Objects.requireNonNull(response.body()).string();

                try {
                    JSONObject json = new JSONObject(myResponse);
                    JSONObject data = json.getJSONObject("data");
                    JSONObject data1 = data.getJSONObject("1");
                    String logoURL = data1.getString("logo");
                    coin.setImageURL(logoURL);
                    Log.v("JSON", logoURL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
