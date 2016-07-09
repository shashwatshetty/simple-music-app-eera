package com.dreams.waking.oeuvre;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;

public class SplashActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private ArrayList<Song> songList;
    private static final int SPLASH_TIME_OUT = 1500;
    private static final int READ_EXTERNAL_STORAGE_CODE = 100;
    private static final String TAG = "SplashActivity";
    public static final String SONG_LIST = "Song List";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //initialise the song array list
        songList = new ArrayList<Song>();
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


    /** Method to retrieve the lis of songs from the internal storage **/
    public void getSongList(){
        //Log.i("MainActivity", "Inside getSongList()");
        //ContentResolver to interact with the audio content provider
        ContentResolver musicResolver = getContentResolver();
        //building the uri where the audio files will be searched for in the storage
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //allowing read/write access to the specified location in the uri to retrieve audio files
        Cursor musicCursor = musicResolver.query(musicUri, null, "IS_MUSIC != 0", null, "TITLE");
        //case when music files are found
        if (musicCursor != null && musicCursor.moveToFirst()){
            //retrieving media column indexes for the required data fields of the audio files
            int idIndex = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleIndex = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            //creating song instances and populating the songList array
            do{
                long songId = musicCursor.getLong(idIndex);
                String songTitle = musicCursor.getString(titleIndex);
                String songArtist = musicCursor.getString(artistIndex);
                //set appropriate name to unknown artist
                if (songArtist.equalsIgnoreCase("<unknown>"))
                    songArtist = "Unknown Artist";
                songList.add(new Song(songId, songTitle, songArtist));
            }while(musicCursor.moveToNext());
        }
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
