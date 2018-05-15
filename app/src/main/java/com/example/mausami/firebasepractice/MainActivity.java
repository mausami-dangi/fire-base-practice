package com.example.mausami.firebasepractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;
import com.example.mausami.firebasepractice.retrofit.APIClient;
import com.example.mausami.firebasepractice.retrofit.APIInterface;
import com.google.gson.JsonObject;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static MessagesListAdapter<Message> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TOKEN = SharedPreferencesHelper.getString(SharedPreferencesHelper.FIREBASE_TOKEN, getApplicationContext());
        // TODO: Change to your receiver's device token
        final String sendTo = TOKEN;

        final MessageInput messageInput = findViewById(R.id.input);
        MessagesList messagesList = findViewById(R.id.messagesList);

        // Initialize adapter
        adapter = new MessagesListAdapter<>(TOKEN, null);
        messagesList.setAdapter(MainActivity.adapter);

        // Initial Welcome message
        Author author = new Author("AECOM-BOT", "AECOM", "");
        Message message = new Message("INITIAL-MESSAGE", "Welcome to AECOM!", author);
        adapter.addToStart(message, true);

        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {

                final String messageBody = messageInput.getInputEditText().getText().toString();

                Author author = new Author(TOKEN, "Ankit", "");
                Message message = new Message("MSG", messageBody, author);
                adapter.addToStart(message, true);

                apiCall(sendTo, messageBody);
                return true;
            }
        });
    }

    /**
     * Call fcm/send for sending message.
     *
     * @param sendTo
     * @param messageBody
     */
    private void apiCall(String sendTo, String messageBody) {
        // Create notification object
        JsonObject notification = new JsonObject();
        notification.addProperty("title", "Got a new notification from FCM");
        notification.addProperty("body", messageBody);

        // Create complete json including notification object
        JsonObject json = new JsonObject();
        json.addProperty("to", sendTo);
        json.add("notification", notification);

        // Call API
        retrofit2.Call<ResponseBody> loadChanges = APIClient.getClient()
                .create(APIInterface.class)
                .sendNotification(json);

        // Load Response
        loadChanges.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("CALL_STATUS", "CALL Success");
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.i("CALL_STATUS", "CALL Fail");
            }
        });
    }
}
