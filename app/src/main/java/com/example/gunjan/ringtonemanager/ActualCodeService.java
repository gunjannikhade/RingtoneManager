package com.example.gunjan.ringtonemanager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gunjan on 03-06-18.
 */

public class ActualCodeService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ActualCodeService() {
        super("ActualCodeService");
    }

    List<Song> audioList = new ArrayList<>();
    List<Song> displayList = new ArrayList<>();


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

        String id;

        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null, null);
        Log.e("", "in service");

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    Log.e("id", audioCursor.getString(0));
                    id = audioCursor.getString(0);
                    Log.e("data no internal", "" + MediaStore.Audio.Media.getContentUriForPath(audioCursor.getString(data)));
                    //     namemap.put(id,audioCursor.getString(audioIndex));
                    audioList.add(new Song(audioCursor.getString(audioIndex), audioCursor.getString(data), id, MediaStore.Audio.Media.getContentUriForPath(audioCursor.getString(data))));
                    displayList.add(new Song(audioCursor.getString(audioIndex), id));
                    Log.i("audioCursor", audioCursor.getString(data));
                    //  audioList.add("Sunday");

                } while (audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        Cursor audioCursorexternal = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);


        if (audioCursorexternal != null) {
            if (audioCursorexternal.moveToFirst()) {
                do {
                    int audioIndex = audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    id = audioCursorexternal.getString(0);
                    Log.e("id", audioCursorexternal.getString(0));
                    Log.e("data no", "" + audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    audioList.add(new Song(audioCursorexternal.getString(audioIndex), audioCursorexternal.getString(data), id, MediaStore.Audio.Media.getContentUriForPath(audioCursorexternal.getString(data))));
                    displayList.add(new Song(audioCursorexternal.getString(audioIndex), id));
                    Log.i("audioCursor", audioCursorexternal.getString(data));

                    //  audioList.add("Sunday");

                } while (audioCursorexternal.moveToNext());
            }
        }
        audioCursorexternal.close();
        changeRingtone();
    }

    public void changeRingtone() {
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
        Log.i("day", simpleDateformat.format(now));
        SharedPreferences sharedPreferences3 = getSharedPreferences("Ringtone Manager",
                Context.MODE_PRIVATE);

        String savedSongName = sharedPreferences3.getString(simpleDateformat.format(now), "");
        Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Song song_here = json.fromJson(savedSongName, Song.class);
        for (Song song : audioList) {
            if (song.getId().equals(song_here.getId())) {
                Uri newuri = ContentUris.withAppendedId(song.getStoragePath(), Long.valueOf(song.getId()));
                RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newuri);


            }
        }
        //    Toast.makeText(getApplicationContext(),"Ringtone set for "+simpleDateformat.format(now)+"is "+song.getSongName(),Toast.LENGTH_SHORT).show();
        Log.e("Check", "Checking if it is there");

    }
//        Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE);
//        Log.e("Default Ringtone is" , ""+ringtone);



}
