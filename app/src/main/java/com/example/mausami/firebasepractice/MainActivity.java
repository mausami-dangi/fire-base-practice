package com.example.mausami.firebasepractice;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;
import com.example.mausami.firebasepractice.retrofit.APIClient;
import com.example.mausami.firebasepractice.retrofit.APIInterface;
import com.google.gson.JsonObject;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    final Context context = this;
    public static MessagesListAdapter<Message> adapter;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

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
        messageInput.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.attachment_dialog);
                dialog.setTitle("Title...");
                dialog.show();

                LinearLayout main_layout = (LinearLayout)dialog.findViewById(R.id.main_layout);
                LinearLayout layout_camera = (LinearLayout)dialog.findViewById(R.id.layout_camera);
                LinearLayout layout_gallery = (LinearLayout)dialog.findViewById(R.id.layout_gallery);
                LinearLayout layout_location = (LinearLayout)dialog.findViewById(R.id.layout_location);
                LinearLayout layout_video_recorder = (LinearLayout)dialog.findViewById(R.id.layout_video_recorder);
                LinearLayout layout_voice_recorder = (LinearLayout)dialog.findViewById(R.id.layout_voice_recorder);
                LinearLayout layout_voice_text =(LinearLayout)dialog.findViewById(R.id.layout_voice_text);

                layout_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSION);
                        }

                        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() +".provider", photoFile);
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(pictureIntent, REQUEST_IMAGE);
                        }

                    }
                });

                layout_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent galleryIntent = new Intent(
//                                Intent.ACTION_PICK,
//                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(galleryIntent , SELECT_IMAGE );
                        dialog.dismiss();
                    }
                });

                layout_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Location Clicked", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                layout_video_recorder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Video Recorder Clicked", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                layout_voice_recorder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Voice Recorder Clicked", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                layout_voice_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Voice to Text Clicked", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
//                imageView.setImageURI(Uri.parse(imageFilePath));
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }
}
