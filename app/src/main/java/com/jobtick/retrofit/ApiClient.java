package com.jobtick.retrofit;



import com.jobtick.utils.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static retrofit2.Retrofit retrofit = null;

    public static ApiInterface getClient() {

        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(new OkHttpClient().newBuilder()
//                            .readTimeout(10, TimeUnit.MINUTES)
//                            .callTimeout(10, TimeUnit.MINUTES)
//                            .connectTimeout(60, TimeUnit.SECONDS)
//                            .writeTimeout(10, TimeUnit.MINUTES)
//                            .build())
                    .build();

        }
        return retrofit.create(ApiInterface.class);
    }


}