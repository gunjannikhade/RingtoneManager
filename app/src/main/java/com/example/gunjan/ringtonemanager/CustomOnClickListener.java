package com.example.gunjan.ringtonemanager;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.sql.BatchUpdateException;
import java.util.List;

/**
 * Created by Gunjan on 17-05-18.
 */

public class CustomOnClickListener implements   View.OnClickListener {

    List<String> list;
    AssetManager assetManager;
    boolean pause=false;
    MediaPlayer player =   new MediaPlayer();

    CustomOnClickListener(List<String> list, AssetManager assetManager)
    {
        this.list=list;
        this.assetManager=assetManager;

    }

    @Override
    public void onClick(View v)
    {
        try {
            Log.d("Why ","why");
          //  AssetFileDescriptor descriptor = assetManager.openFd("temp/" + list.get(0));
            MainActivity m1=new MainActivity();
//            MediaPlayer ring= MediaPlayer.create(m1.getApplicationContext(),R.raw.bhb);
       //     MediaPlayer ring1= MediaPlayer.create(m1.getApplicationContext(),R.raw.);

//            long start = descriptor.getStartOffset();
//            long end = descriptor.getLength();
//
//            ring.setDataSource(descriptor.getFileDescriptor(), start, end);
//            ring.prepare();
//
//            ring.setVolume(1.0f, 1.0f);
//            Log.d("Why ", String.valueOf(player.isP h\laying()));
//            if(player.isPlaying())
//                player.pause();
//            else
    //            ring.start();
        }catch(Exception e){

    }

    }
}
