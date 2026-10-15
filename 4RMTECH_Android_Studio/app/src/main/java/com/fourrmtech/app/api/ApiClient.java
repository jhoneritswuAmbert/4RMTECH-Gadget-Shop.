package com.fourrmtech.app.api;

import retrofit2.Retrofit;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import com.fourrmtech.app.api.ApiClient;

public class ApiClient {

    private static final String BASE_URL =
            "http://10.0.2.2/gadget_shop/";
    public static String getBaseUrl() { return BASE_URL; }
    private static Retrofit retrofit;

    public static Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(new OkHttpClient.Builder().addInterceptor(chain -> {
                        okhttp3.Request.Builder request = chain.request().newBuilder();
                        String token = AuthStore.getToken();
                        if (!token.isEmpty()) request.header("Authorization", "Bearer " + token);
                        return chain.proceed(request.build());
                    }).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}