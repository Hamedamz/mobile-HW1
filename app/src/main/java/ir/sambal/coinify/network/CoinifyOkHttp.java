package ir.sambal.coinify.network;

import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import ir.sambal.coinify.BuildConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CoinifyOkHttp {
    public static OkHttpClient create(Context context) {
        Cache cache = new Cache(
                new File(context.getApplicationContext().getCacheDir(), "http_cache"),
                50L * 1024L * 1024L // 50 MiB
        );
        return new OkHttpClient.Builder().cache(cache).
                callTimeout(5, TimeUnit.SECONDS).
                connectTimeout(5, TimeUnit.SECONDS).
                addInterceptor((chain) -> {
                    Request request = chain.request();
                    Request newRequest;

                    try {
                        newRequest = request.newBuilder()
                                .addHeader("X-CMC_PRO_API_KEY", BuildConfig.X_CMC_PRO_API_KEY)
                                .addHeader("X-CoinAPI-Key", BuildConfig.X_CoinAPI_Key)
                                .build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return chain.proceed(request);
                    }
                    return chain.proceed(newRequest);


                }).build();
    }
}
