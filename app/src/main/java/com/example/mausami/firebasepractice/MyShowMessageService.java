package com.example.mausami.firebasepractice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;

import static com.example.mausami.firebasepractice.MainActivity.adapter;


public class MyShowMessageService extends Service {
    public MyShowMessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            String from = SharedPreferencesHelper.getString(SharedPreferencesHelper.MESSAGE_FROM, getApplicationContext());
            String messageBody = SharedPreferencesHelper.getString(SharedPreferencesHelper.MESSAGE_BODY, getApplicationContext());
            Author author = new Author("AECOM-BOT", "AECOM", "");
            Message message = new Message(from, messageBody, author);
            adapter.addToStart(message, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
