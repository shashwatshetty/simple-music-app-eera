package com.dreams.waking.oeuvre;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import java.util.ArrayList;

public class SplashActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private ArrayList<Song> songList;
    private SongRetriever songRetriever;
    private static final int SPLASH_TIME_OUT = 1500;
    private static final int READ_EXTERNAL_STORAGE_CODE = 100;
    private static final String TAG = "SplashActivity";
    public static final String SONG_LIST = "Song List";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //requesting runtime permissions from Android M
        if(requestRuntimePermission())
            startMainActivity();
    }

    /** Method of the ActivityCompat.OnRequestPermissionsResultCallback class **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMainActivity();
        }
    }

    /** Method to handle permission requests **/
    private boolean requestRuntimePermission(){
        //case when permission is not yet granted
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                return true;
            else{
                //requesting runtime permission to read storage to the app
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
                return false;
            }
        }
        else
            return true;
    }


    /** Method to retrieve the list of songs from the internal storage **/
    public void getSongList(){
        //create object of SongRetriever to get all songs
        songRetriever = SongRetriever.getSongRetrieverInstance(getContentResolver());
        songRetriever.retrieveSongs();
        songList = songRetriever.getAllSongs();
    }

    private void startMainActivity(){
        getSongList();
        //display Splash Screen for 2.5 seconds
        new Handler().postDelayed(new Runnable() {

            /** Method executed after the delay **/
            @Override
            public void run() {
                //create intent to call the next activity
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                //package the songList into the intent
                mainIntent.putParcelableArrayListExtra(SONG_LIST, songList);
                startActivity(mainIntent);
                //finish the current activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
