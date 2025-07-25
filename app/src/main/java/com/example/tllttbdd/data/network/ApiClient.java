package com.example.tllttbdd.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Tạo HTTP logging interceptor để debug
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            // Tạo OkHttpClient với logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            // Tạo Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000/") // Địa chỉ backend Node.js cho Android emulator
                    .addConverterFactory(GsonConverterFactory.create()) //Gson tự động parse thành  object
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}