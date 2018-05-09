package com.example.mausami.firebasepractice.retrofit;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIInterface {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAJVZ_mbs:APA91bFmCZThQtIu5DACz0I9iMG_7Bp14iZApeouF4jbuN_2BwN66hjwErjrMR2BHCR6F7Kkb_L7QU13NXCQxEn_GRh3LjrHwpy69LoCwgYdSdBjhINs0-Hseq4oHpZZ-MQYt3Zflo_Y"
    })
    @POST("/fcm/send")
    Call<ResponseBody> sendNotification(@Body JsonObject json);
}
