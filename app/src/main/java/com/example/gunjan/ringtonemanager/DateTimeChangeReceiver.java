package com.example.gunjan.ringtonemanager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gunjan on 27-05-18.
 */

public class DateTimeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("here", "in Broadcast reciever");
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
//            MainActivity m1=new MainActivity();
//            m1.changeRingtone();
            Log.e("here", "ACTION_DATE_CHANGED received");
            Toast.makeText(context, "Date changed by Gunjan", Toast.LENGTH_SHORT).show();

        }



    }
}
