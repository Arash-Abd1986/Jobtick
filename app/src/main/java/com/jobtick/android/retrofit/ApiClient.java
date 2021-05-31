package com.jobtick.android.retrofit;



import com.jobtick.android.utils.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static retrofit2.Retrofit retrofit = null;
    private retrofit2.Retrofit retrofitVideoUpload;

    public static ApiInterface getClient() {

        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient().newBuilder()
                            .readTimeout(15, TimeUnit.SECONDS)
                            .callTimeout(15, TimeUnit.SECONDS)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .build())
                    .build();

        }
        return retrofit.create(ApiInterface.class);
    }

    public static ApiInterface getClientV2() {

        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL_v2)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient().newBuilder()
                            .readTimeout(15, TimeUnit.SECONDS)
                            .callTimeout(15, TimeUnit.SECONDS)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .build())
                    .build();

        }
        return retrofit.create(ApiInterface.class);
    }


    //we don't need this object in the whole of the app, so we avoid to use it as singleton
    public ApiInterface buildAndGetClientForVideoUpload() {

        if (retrofitVideoUpload == null) {

            retrofitVideoUpload = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient().newBuilder()
                            .readTimeout(2, TimeUnit.MINUTES)
                            .callTimeout(2, TimeUnit.MINUTES)
                            .connectTimeout(2, TimeUnit.MINUTES)
                            .writeTimeout(2, TimeUnit.MINUTES)
                            .build())
                    .build();

        }
        return retrofitVideoUpload.create(ApiInterface.class);
    }
}