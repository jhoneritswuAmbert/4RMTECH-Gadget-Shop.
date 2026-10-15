package com.fourrmtech.app.api;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class AuthStore {
    private static SharedPreferences preferences;
    public static void initialize(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences("GadgetShopAuth", Context.MODE_PRIVATE);
    }
    public static String getToken() { return preferences == null ? "" : preferences.getString("token", ""); }
    public static void save(String token) {
        if (preferences != null) preferences.edit().putString("token", token == null ? "" : token).apply();
    }
    public static void logout() {
        String token = getToken();
        save("");
        if (token.isEmpty()) return;
        Request request = new Request.Builder().url(ApiClient.getBaseUrl() + "logout.php")
                .header("Authorization", "Bearer " + token).post(RequestBody.create(null, new byte[0])).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { }
            @Override public void onResponse(Call call, Response response) { response.close(); }
        });
    }
}
