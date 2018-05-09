package com.example.mausami.firebasepractice.retrofit;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @POST("/fcm/send")
    Call<ResponseBody> loadChanges(@Query("to") String to, @Query("notification") JsonObject jsonObject);
}
