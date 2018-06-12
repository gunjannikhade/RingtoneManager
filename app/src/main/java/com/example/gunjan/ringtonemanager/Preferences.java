package com.example.gunjan.ringtonemanager;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String DEFAULT_RINGTONE_PREF_NAME = "defaultRingtone";
    private static final String RINGTONE_MANAGER_PREF_NAME = "ringtoneManager";

    private static final String DEFAULT_RINGTONE_KEY = "keyDefaultRingtone";

    private static SharedPreferences getDefaultRingtonePreferences(Context context) {
        return context.getSharedPreferences(DEFAULT_RINGTONE_PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getRingtoneManagerPreferences(Context context) {
        return context.getSharedPreferences(RINGTONE_MANAGER_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setDefaultRingtone(Context context, String defaultRingtone) {
        SharedPreferences sharedPreferences =  getDefaultRingtonePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_RINGTONE_KEY, defaultRingtone);
        editor.apply();
    }

    public static String getDefaultRingtone(Context context) {
        SharedPreferences sharedPreferences =  getDefaultRingtonePreferences(context);
        return sharedPreferences.getString(DEFAULT_RINGTONE_KEY, null);
    }

    public static void setRingtoneForSelectedDay(Context context, String day, String selectedRingtoneName) {
        SharedPreferences sharedPreferences =  getRingtoneManagerPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(day, selectedRingtoneName);
        editor.apply();
    }

    public static String getRingtoneForSelectedDay(Context context, String day) {
        SharedPreferences sharedPreferences =  getRingtoneManagerPreferences(context);
        return sharedPreferences.getString(day, null);
    }

}
