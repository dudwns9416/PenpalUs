package com.sc.fopa.penpalus.service;

import com.sc.fopa.penpalus.domain.FcmResponse;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by fopa on 2017-12-14.
 */

public interface FcmService {

    @POST("send")
    Call<FcmResponse> sendFcm(
            @Body RequestBody body
    );
}
