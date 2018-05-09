package com.example.mausami.firebasepractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String TOKEN = SharedPreferencesHelper.getString(SharedPreferencesHelper.FIREBASE_TOKEN, getApplicationContext());

        final TextView txtResponse = findViewById(R.id.txtResponse);
        final EditText txtMessage = findViewById(R.id.txtMessage);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);

        txtResponse.setText("Your Firebase token... \n\n"+TOKEN);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
