package com.example.gunjan.ringtonemanager;

import android.net.Uri;

import java.net.URI;

/**
 * Created by Gunjan on 19-05-18.
 */

public class Song {
    String songName;
    String Path;
    String id;
    Uri storagePath;
    public void setId(String id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public String getId() {
        return id;
    }

    public Uri getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(Uri storagePath) {
        this.storagePath = storagePath;
    }

    public Song(String songName, String path, String id, Uri storagePath) {
        this.songName = songName;
        Path = path;
        this.id=id;
        this.storagePath=storagePath;

    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }
}
