package com.example.promemory;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notification extends Application {

    public static final String notificationID = "notification";

    @Override
    public void onCreate(){
        super.onCreate();

        //createNotificationChannel();
    }

    public void createNotificationChannel(String message){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    notificationID,
                    "notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.enableVibration(true);
            channel.setDescription(message);
        }
    }
}
