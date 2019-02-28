package com.sc.fopa.penpalus.service;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sc.fopa.penpalus.utils.WsConfig;
import com.sc.fopa.penpalus.domain.Papago;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MessageService {
    @FormUrlEncoded
    @POST("n2mt")
    Call<Papago> koreanTranslateEnglish(
            @Field("source") String source,
            @Field("target") String target,
            @Field("text") String text
    );




}
