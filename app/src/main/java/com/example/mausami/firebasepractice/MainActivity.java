package com.example.mausami.firebasepractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TOKEN = SharedPreferencesHelper.getString(SharedPreferencesHelper.FIREBASE_TOKEN, getApplicationContext());

        final MessageInput messageInput = findViewById(R.id.input);
        final MessagesList messagesList = findViewById(R.id.messagesList);

        final MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(TOKEN, null);
        messagesList.setAdapter(adapter);

        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
                Author author = new Author(TOKEN, "Ankit", "");
                Message message = new Message("MSG01", messageInput.getInputEditText().getText().toString(), author);
                adapter.addToStart(message, true);
                return true;
            }
        });
    }
}
