package com.example.gunjan.ringtonemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("debugging", "Kuch to huva");

        if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
            Intent intent1=new Intent(context,ActualCodeService.class);
            context.startService(intent1);
            Log.i("", "Ringtone Changed Application Called");
        }
    }
}

