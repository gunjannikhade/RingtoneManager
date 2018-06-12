package com.example.gunjan.ringtonemanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SYSTEM_PERMISSION_FLAG = 1001;
    private static final int STORAGE_PERMISSION_FLAG = 1002;
    final String[] mDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    String mSelectedDay;

    List<Song> mRingtoneList = new ArrayList<>();
    List<Song> mDisplayList = new ArrayList<>();
    MediaPlayer mMediaPlayer;
    Song mSong;

    LinearLayout mMainView, mProgressView;
    View main;
    Spinner mDaysSpinner, mRingtoneSpinner;
    ImageButton mPlayAndStopRingtone;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_and_stop_ringtone:
                playAndStopRingtone();
                break;
            case R.id.set_ringtone:
                setRingtone();
                break;
            case R.id.reset_ringtone:
                resetRingtone();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "3 requestCode " + requestCode);
        switch (requestCode) {
            case SYSTEM_PERMISSION_FLAG:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    showSnackBar();
                }
                break;
            case STORAGE_PERMISSION_FLAG:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "4 requestCode " + requestCode);
                    actualCode();
                } else {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Log.e(TAG, "5 requestCode " + requestCode);
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                        Log.e(TAG, showRationale + " ");
                        if (!showRationale) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Without this permission the app is unable to read the audio file from the storage. \n\nTo grant the permission click on RE-TRY --> Permissions --> Storage. \n\nTo deny permission Click on I'M SURE.")
                                    .setCancelable(false)
                                    .setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("RE-TRY", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                                            startActivityForResult(intent, STORAGE_PERMISSION_FLAG);
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.setTitle("Permission denied");
                            alert.show();
                        } else {
                            checkPermissions();
                        }
                    }


                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, " requestCode " + requestCode);
        switch (requestCode) {
            case SYSTEM_PERMISSION_FLAG:
                if (Settings.System.canWrite(this)) {
                    Log.d("TAG", "CODE_WRITE_SETTINGS_PERMISSION success");
                    checkPermissions();
                } else {
                    showSnackBar();
                }
                break;
            case STORAGE_PERMISSION_FLAG:
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "1 requestCode " + requestCode);

                    actualCode();
                } else {
                    Log.e(TAG, " requestCode 2" + requestCode);
                    checkPermissions();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permission", "Permission is granted1");
            actualCode();
        } else {
            Log.v("Permission", "Permission is revoked1");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_FLAG);
        }
    }

    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Log.i(TAG, "time set is : " + calendar.getTime());

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void actualCode() {
        new LoadAudio().execute();
    }

    private void setDefaultRingtone() {
        Uri ringtone = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE);
        String newSongName = null;
        for (Song song : mRingtoneList) {
            if (ContentUris.withAppendedId(song.getStoragePath(), Long.valueOf(song.getId())).equals(ringtone)) {
                Song newSong = new Song(song.getSongName(), song.getId());
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                newSongName = gson.toJson(newSong);
                Preferences.setDefaultRingtone(MainActivity.this, newSongName);
                break;
            }
        }
        for (String day : mDays) {
            Preferences.setRingtoneForSelectedDay(MainActivity.this, day, newSongName);
        }
    }

    private void initViews() {
        mMainView = findViewById(R.id.mainView);
        mProgressView = findViewById(R.id.progressBarView);
        main = findViewById(R.id.mainLayout);

        mPlayAndStopRingtone = findViewById(R.id.play_and_stop_ringtone);
        ImageButton setRingtone = findViewById(R.id.set_ringtone);
        ImageButton resetRingtone = findViewById(R.id.reset_ringtone);

        mPlayAndStopRingtone.setOnClickListener(this);
        setRingtone.setOnClickListener(this);
        resetRingtone.setOnClickListener(this);

        showSystemPermissionView();
    }

    private void initDaySpinner() {
        mDaysSpinner = findViewById(R.id.daysSpinner);
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, mDays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDaysSpinner.setAdapter(adapter);
        mDaysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedDay = parent.getItemAtPosition(position).toString();
                int pos = getSongsPosition(mSelectedDay);
                mRingtoneSpinner.setSelection(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initRingtoneSpinner() {
        mRingtoneSpinner = findViewById(R.id.ringtoneSpinner);
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, mDisplayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRingtoneSpinner.setAdapter(adapter);
        mSong = mDisplayList.get(0);
        mRingtoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSong = (Song) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void playAndStopRingtone() {
        try {
            String path;
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mPlayAndStopRingtone.setImageDrawable(getResources().getDrawable(R.drawable.start));
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer = null;

            } else {
                for (Song song : mRingtoneList) {
                    if (song.getId().equals(mSong.getId())) {
                        mPlayAndStopRingtone.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                        path = song.getPath();
                        if (mMediaPlayer == null) {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setDataSource(path);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                        }
                    }
                }
            }

            if (mMediaPlayer != null) {
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayAndStopRingtone.setImageDrawable(getResources().getDrawable(R.drawable.start));
                        mp.stop();
                        mp.reset();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRingtone() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String song = gson.toJson(mSong);
        Preferences.setRingtoneForSelectedDay(MainActivity.this, mSelectedDay, song);
        Utils.changeRingtone(getApplicationContext(), mRingtoneList, mSong);
        Snackbar.make(main, "Ringtone set for " + mSelectedDay + " is " + mSong.getSongName(), Snackbar.LENGTH_LONG).show();
    }

    private void resetRingtone() {
        String defaultRingtone = Preferences.getDefaultRingtone(MainActivity.this);
        if (defaultRingtone != null) {
            Preferences.setRingtoneForSelectedDay(MainActivity.this, mSelectedDay, defaultRingtone);
            Gson json = new Gson();
            Song song = json.fromJson(defaultRingtone, Song.class);
            Utils.changeRingtone(getApplicationContext(), mRingtoneList, song);
            mRingtoneSpinner.setSelection(0);
            Snackbar.make(main, "Ringtone reset to " + song.getSongName() + " for " + mSelectedDay, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(main, "Ringtone reset to silent for " + mSelectedDay, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSystemPermissionView() {
        boolean writeSettingsPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            writeSettingsPermission = Settings.System.canWrite(this);
        } else {
            writeSettingsPermission = ContextCompat.checkSelfPermission
                    (this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (writeSettingsPermission) {
            checkPermissions();
        } else {
            showSnackBar();
        }
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(main, "Please provide the system permission", Snackbar.LENGTH_INDEFINITE)
                .setAction("Proceed", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                            MainActivity.this.startActivityForResult(intent, SYSTEM_PERMISSION_FLAG);
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, SYSTEM_PERMISSION_FLAG);
                        }
                    }
                });
        snackbar.show();
    }

    private int getSongsPosition(String day) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = Preferences.getRingtoneForSelectedDay(MainActivity.this, day);
        Song song = gson.fromJson(json, Song.class);
        return mDisplayList.indexOf(song);
    }

    private class LoadAudio extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Utils.readRingtoneFromInternalStorage(getApplicationContext(), mRingtoneList, mDisplayList);
            Utils.readRingtoneFromExternalStorage(getApplicationContext(), mRingtoneList, mDisplayList);

            setDefaultRingtone();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                initDaySpinner();
                initRingtoneSpinner();
                setAlarm();
                mProgressView.setVisibility(View.GONE);
                mMainView.setVisibility(View.VISIBLE);
            }
        }
    }
}