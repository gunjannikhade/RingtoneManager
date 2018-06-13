package com.example.gunjan.ringtonemanager;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.List;

public class Utils {

    public static void readRingtoneFromInternalStorage(Context context, List<Song> ringtoneList, List<Song> displayList) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int audioIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    String id = cursor.getString(0);
                    ringtoneList.add(new Song(cursor.getString(audioIndex),
                            cursor.getString(data), id,
                            MediaStore.Audio.Media.getContentUriForPath(cursor.getString(data))));
                    displayList.add(new Song(cursor.getString(audioIndex),id));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public static void readRingtoneFromExternalStorage(Context context, List<Song> ringtoneList, List<Song> displayList) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int audioIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    String id = cursor.getString(0);
                    ringtoneList.add(new Song(cursor.getString(audioIndex),
                            cursor.getString(data), id,
                            MediaStore.Audio.Media.getContentUriForPath(cursor.getString(data))));
                    displayList.add(new Song(cursor.getString(audioIndex),id));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public static void changeRingtone(Context context, List<Song> ringtoneList, Song song) {
        for (Song ringtone : ringtoneList) {
            if (ringtone.getId().equals(song.getId())) {
                Uri uri = ContentUris.withAppendedId(ringtone.getStoragePath(), Long.valueOf(song.getId()));
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, uri);
            }
        }
    }

}
