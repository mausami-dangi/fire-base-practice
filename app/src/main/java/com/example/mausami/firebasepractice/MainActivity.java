package com.example.mausami.firebasepractice;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;
import com.example.mausami.firebasepractice.retrofit.APIClient;
import com.example.mausami.firebasepractice.retrofit.APIInterface;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] TOKEN = {SharedPreferencesHelper.getString(SharedPreferencesHelper.FIREBASE_TOKEN, getApplicationContext())};

        final TextView txtResponse = findViewById(R.id.txtResponse);
        final EditText txtMessage = findViewById(R.id.txtMessage);
        final EditText txtDeviceToken = findViewById(R.id.txtDeviceToken);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);
        Button btnCopyToken = findViewById(R.id.btnCopyToken);
        final String[] message = {""};

        txtResponse.setText("Your Firebase token... \n\n"+ TOKEN[0]);

        // Set txtMessage[EditText] textChangeListener to get message
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                message[0] = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set txtDeviceToken[EditText] textChangeListener to update token
        txtDeviceToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    TOKEN[0] = s.toString();
                }
                else {
                    TOKEN[0] = SharedPreferencesHelper.getString(SharedPreferencesHelper.FIREBASE_TOKEN, getApplicationContext());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set btnCopyToken[Button] clickListener to copy token in Clipboard
        btnCopyToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FIREBASE_TOKEN", SharedPreferencesHelper.getString(SharedPreferencesHelper.FIREBASE_TOKEN, getApplicationContext()));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Token copied successfully", Toast.LENGTH_LONG).show();
            }
        });

        // Set btnSendMessage[Button] clickListener to call API [POST] /fcm/send
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create notification object
                JsonObject notification = new JsonObject();
                notification.addProperty("title", "Got a new notification from FCM");
                notification.addProperty("body", "Message: "+ message[0]);

                // Create complete json including notification object
                JsonObject json = new JsonObject();
                json.addProperty("to", TOKEN[0]);
                json.add("notification", notification);

                txtResponse.setText("Sending notification to... \n\n"+ TOKEN[0]);

                // Call API
                retrofit2.Call<ResponseBody> loadChanges = APIClient.getClient()
                        .create(APIInterface.class)
                        .sendNotification(json);

                // Load Response
                loadChanges.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200) {
                            txtResponse.setText("Notification sent...");
                            txtResponse.append("\n\nSent to: "+ TOKEN[0]);
                            txtResponse.append("\n\nMessage: " + message[0]);
                            txtResponse.append("\n\nResponse code: "+ response.code());
                            txtMessage.setText("");
                        }
                        else {
                            txtResponse.setText(response.toString());
                        }
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
