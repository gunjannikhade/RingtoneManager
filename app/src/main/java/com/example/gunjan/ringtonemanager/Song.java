package com.example.gunjan.ringtonemanager;

import android.net.Uri;

import com.google.gson.annotations.Expose;

public class Song {
    @Expose
    String songName;
    String Path;
    @Expose
    String id;
    Uri storagePath;
    int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

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

    public Song(String songName, String id) {
        this.songName = songName;
        this.id = id;
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

    @Override
    public String toString() {
        return songName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Song){
            Song c = (Song ) obj;
            if(c.getId().equals(id)) return true;
        }

        return false;
    }
}
