package com.example.mausami.firebasepractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;
import com.example.mausami.firebasepractice.retrofit.APIClient;
import com.example.mausami.firebasepractice.retrofit.APIInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TOKEN = SharedPreferencesHelper.getString(SharedPreferencesHelper.FIREBASE_TOKEN, getApplicationContext());

        final TextView txtResponse = findViewById(R.id.txtResponse);
        final EditText txtMessage = findViewById(R.id.txtMessage);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);

        txtResponse.setText("Your Firebase token... \n\n"+TOKEN);

        JsonObject notification = new JsonObject();
        notification.addProperty("title", "Ankit Thakur");
        notification.addProperty("body", "Got a new notification from FCM");

        final JsonObject json = new JsonObject();
        json.addProperty("to", TOKEN);
        json.add("notification", notification);


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrofit2.Call<ResponseBody> loadChanges = APIClient.getClient()
                        .create(APIInterface.class)
                        .sendNotification(json);

                loadChanges.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                        txtResponse.setText(response.toString());
                        txtMessage.setText("");
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        Log.d("CALL_STATUS", "CALL Fail");
                    }
                });
            }
        });
    }
}
