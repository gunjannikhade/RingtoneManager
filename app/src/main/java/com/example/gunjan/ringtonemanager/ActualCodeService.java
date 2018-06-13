package com.example.gunjan.ringtonemanager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActualCodeService extends IntentService {
    List<Song> mRingtoneList = new ArrayList<>();
    List<Song> displayList= new ArrayList<>();

    public ActualCodeService() {
        super("ActualCodeService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Changing ringtone",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Utils.readRingtoneFromInternalStorage(getApplicationContext(), mRingtoneList, displayList);
        Utils.readRingtoneFromExternalStorage(getApplicationContext(), mRingtoneList, displayList);
        changeRingtone();
    }

    public  void changeRingtone(){
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        String selectedDay = simpleDateformat.format(now);
        String songName =Preferences.getRingtoneForSelectedDay(getApplicationContext(), selectedDay);
        Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Song song = json.fromJson(songName, Song.class);
        Utils.changeRingtone(getApplicationContext(), mRingtoneList, song);
        Toast.makeText(getApplicationContext(),"Ringtone Set for " + selectedDay + " is " + songName ,Toast.LENGTH_SHORT).show();
    }

}
