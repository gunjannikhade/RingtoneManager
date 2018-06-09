package com.example.gunjan.ringtonemanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.R.attr.password;


public class MainActivity extends Activity {

    LinearLayout main;
    RelativeLayout permission;
    List<Song> audioList = new ArrayList<>();

    String day;

    ImageButton button;
    String path;
    String songName;
    boolean permission1;
    String ringtone;
    String id;
    Map<String,String> namemap =new HashMap<>();
    TextView demo;
    String defaultSongName;


    private PendingIntent pendingIntent;
    private AlarmManager manager;
    final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences1;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences1 = getSharedPreferences("Default Ringtone",
                Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("Ringtone Manager",
                Context.MODE_PRIVATE);


        main = (LinearLayout) findViewById(R.id.mainView);
        permission = (RelativeLayout) findViewById(R.id.permissionView);
        checkPermissions();
        demo =(TextView)findViewById(R.id.text2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.i("time set is",""+calendar.getTime());

        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
        //intent.setAction("ax.android.mybroadcast");
       // sendBroadcast(intent);
        //IntentFilter intentFilter=new IntentFilter("ax.android.mybroadcast");
        //AlarmReceiver1 broadcastReceiver=new AlarmReceiver1();
        //this.registerReceiver(broadcastReceiver,intentFilter);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        //  alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent0);
      //  Toast.makeText(MainActivity.this, "Alarm Scheduled for Tomorrow ", Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission1 = Settings.System.canWrite(this);
        } else {
            permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission1) {
            //do your code
        }  else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent1 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent1.setData(Uri.parse("package:" + this.getPackageName()));
                this.startActivityForResult(intent1, 101);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, 101);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
            case 101:
                Log.d("Permission ", "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    permission.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                    actualCode();
                    //resume tasks needing this permission
                }
                    break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission", "Permission is granted1");
                permission.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
                actualCode();


            } else {

                Log.v("Permission", "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }


        }

    }

    public void actualCode() {
      //  listRingtones();



//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
//        registerReceiver(new DateTimeChangeReceiver(), intentFilter);

        Spinner spinner_days = (Spinner) findViewById(R.id.spinner13);
        List<String> list = new LinkedList<>(Arrays.asList(days));
        Log.d("list ", list.get(0));
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_days.setAdapter(adapter1);

        String fileList[];

        Spinner spinner = (Spinner) findViewById(R.id.spinner14);
        AssetManager assetManager = getAssets();

        // Memory song code


        //  String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME};// Can include more data for more details and check it.
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null, null);

        path = MediaStore.Audio.Media.DATA;
        Log.i("Path is ", path);
        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    Log.e("id", audioCursor.getString(0));
                    id=audioCursor.getString(0);
                    Log.e("data no internal",""+MediaStore.Audio.Media.getContentUriForPath(audioCursor.getString(data)));
                    namemap.put(id,audioCursor.getString(audioIndex));
                    audioList.add(new Song(audioCursor.getString(audioIndex), audioCursor.getString(data),id,MediaStore.Audio.Media.getContentUriForPath(audioCursor.getString(data))));

                    Log.i("audioCursor", audioCursor.getString(data));
                    //  audioList.add("Sunday");

                } while (audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        Cursor audioCursorexternal = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        path = MediaStore.Audio.Media.DATA;
        Log.i("Path is ", path);
        if (audioCursorexternal != null) {
            if (audioCursorexternal.moveToFirst()) {
                do {
                    int audioIndex = audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    id=audioCursorexternal.getString(0);
                    Log.e("id", audioCursorexternal.getString(0));
                    Log.e("data no",""+audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    audioList.add(new Song(audioCursorexternal.getString(audioIndex), audioCursorexternal.getString(data),id,MediaStore.Audio.Media.getContentUriForPath(audioCursorexternal.getString(data))));

                    Log.i("audioCursor", audioCursorexternal.getString(data));
                    //  audioList.add("Sunday");

                } while (audioCursorexternal.moveToNext());
            }
        }
        audioCursorexternal.close();
        //listRingtones();

        List<String> displayName = new ArrayList<>();
        for (Song list11 : audioList) {
            displayName.add(list11.getSongName());
        }
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,displayName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        setDefaultRingtone();


//        SharedPreferences sharedPreferences2 = getSharedPreferences("Ringtone Manager",
//                Context.MODE_PRIVATE);
//        String savedSongName=sharedPreferences2.getString("Sunday","");
//        Log.i("testing",savedSongName);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                songName = parent.getItemAtPosition(position).toString();
                Log.i("Text ", songName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = parent.getItemAtPosition(position).toString();
                Log.i("Selected day is ", day);

                demo.setText("Ringtone Set for "+day+ " is "+sharedPreferences.getString(day,""));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//Button Code
        button =  findViewById(R.id.button8);
        ImageButton button_stop =  findViewById(R.id.button9);
        ImageButton button_reset =  findViewById(R.id.button10);

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences4 = getSharedPreferences("Default Ringtone",
                        Context.MODE_PRIVATE);
                String defaults=sharedPreferences4.getString("defaultRingtone","");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(day,defaults);
                editor.apply();
                changeRingtone(audioList);
                demo.setText("Ringtone Set for "+day+ " is "+sharedPreferences.getString(day,""));


            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            // MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dilbaro);
            MediaPlayer mp = new MediaPlayer();



            public void onClick(View v) {

                try {
                    String path;
                    Log.i("Is Playing", "" + mp.isPlaying());
                    if (mp.isPlaying()) {
                        button.setImageDrawable(getResources().getDrawable(R.drawable.play_32));

                        // button.setText("Play");
                        mp.stop();
                        mp.reset();

                    } else {
                        for (Song song : audioList) {
                            if (song.getSongName().equals(songName)) {
//                                button.setText("Stop");
                                button.setImageDrawable(getResources().getDrawable(R.drawable.stop_32));

                                path = song.getPath();
                                mp.setDataSource(path);
                                mp.prepare();
                                mp.start();

                            }
                        }
                    }

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            button.setImageDrawable(getResources().getDrawable(R.drawable.play_32));
                            Log.i("Completion Listener", "Song Complete");
                            mp.stop();
                            mp.reset();


                        }
                    });
//                    Intent intent=new Intent("ax.android.mybroadcast");
//                    MainActivity.this.sendBroadcast(intent);
                    //   Log.i("Media ",MediaStore.Audio.Media.EXTERNAL_CONTENT_URI+"/"+songName.substring(0,));
                    //   mp.setDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI+"/"+songName);
                    //    int resID=getResources().getIdentifier("bhb", "raw", getPackageName());
//                    if (mediaPlayer.isPlaying())
//                        mediaPlayer.pause();
//                    else
//                        mediaPlayer.start();
                } catch (Exception e) {

                }

            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Here","here");
                try {
//                for (Song song : audioList) {
//                    if (song.getSongName().equals(songName)) {
//                        Uri newuri = ContentUris.withAppendedId(song.getStoragePath(), Long.valueOf(song.getId()));
//                        RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newuri);
//                        Toast.makeText(getApplicationContext(),"Ringtone is set to "+song.getSongName(),Toast.LENGTH_SHORT).show();
//                        Log.e("Check","Checking if it is there");
//                    }
//
//                }
//                    Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE);
//                    Log.e("Default Ringtone is" , ""+ringtone);

                    //sharedPreferences = getApplicationContext().getSharedPreferences("Ringtone Details", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(day,songName);
                    editor.apply();
                    demo.setText("Ringtone Set for "+day+ " is "+sharedPreferences.getString(day,""));

                    String song=changeRingtone(audioList);
                    Toast.makeText(getApplicationContext(),"Ringtone set for "+day+" is "+sharedPreferences.getString(day,""),Toast.LENGTH_SHORT).show();


//                    Calendar calendar = Calendar.getInstance();
//                    int day = calendar.get(Calendar.DAY_OF_WEEK);


                    Calendar now = Calendar.getInstance();
                    Log.i("time",""+now.getTime());
                    Log.i("Hour",""+now.get(Calendar.HOUR_OF_DAY));
                    Log.i("Min",""+now.get(Calendar.MINUTE));
                    Log.i("day",""+now.get(Calendar.DAY_OF_WEEK));

                }
                catch (Throwable t) {


                }

            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && Settings.System.canWrite(this)){
            Log.d("TAG", "CODE_WRITE_SETTINGS_PERMISSION success");
            //do your code
        }
    }


    public  String changeRingtone(List<Song> audioList1){

//        SharedPreferences sharedPreferences = getSharedPreferences("Ringtone Manager",
//                Context.MODE_PRIVATE);
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
        Log.i("day",simpleDateformat.format(now));
        SharedPreferences sharedPreferences3 = getSharedPreferences("Ringtone Manager",
                Context.MODE_PRIVATE);
        String savedSongName=sharedPreferences3.getString(simpleDateformat.format(now),"");

        for (Song song : audioList1) {
            Log.i("Song here is ",song.getSongName());
            Log.i("Song Name here is",songName);
            if (song.getSongName().equals(savedSongName)) {
                Log.i("value of day is ",sharedPreferences.getString(simpleDateformat.format(now),""));
               // Long.valueOf(sharedPreferences.getString(simpleDateformat.format(now),""));
                Uri newuri = ContentUris.withAppendedId(song.getStoragePath(), Long.valueOf(song.getId()));
                RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newuri);
            //    Toast.makeText(getApplicationContext(),"Ringtone set for "+simpleDateformat.format(now)+"is "+song.getSongName(),Toast.LENGTH_SHORT).show();
                Log.e("Check","Checking if it is there");
                return  song.getSongName();

            }

        }
        return "";
//        Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE);
//        Log.e("Default Ringtone is" , ""+ringtone);
    }

    private void setDefaultRingtone()
    {
        Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE);

        Log.e("Default Ringtone is" , ""+ringtone);
        Log.i("haha","haha");
        for (Song song : audioList) {
            Log.i("song.getStoragePath()",song.getStoragePath().toString());
            Log.i("ringtone",ringtone.toString());

            if (ContentUris.withAppendedId(song.getStoragePath(), Long.valueOf(song.getId())).equals(ringtone)) {
                defaultSongName=song.getSongName();
                break;
            }
            }
                    if((sharedPreferences1.getString("defaultRingtone","").equals(""))) {
                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                        editor1.putString("defaultRingtone", defaultSongName);
                        editor1.commit();
                        for (String day : days) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(day, defaultSongName);
                            editor.apply();
                        }

                    }
            }

}