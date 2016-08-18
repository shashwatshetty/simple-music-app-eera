package com.dreams.waking.oeuvre;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener/* MediaController.MediaPlayerControl*/{

    private static Song currentSong;
    private Intent serviceIntent;
    private int songPosition;
    private ArrayList<Song> listOfSongs;
    private TextView currentSongTitle, currentSongArtist;
    private ImageButton playButton, pauseButton, prevButton, nextButton;
    private static final String SONG_LIST = "Song List";
    private static final String CURRENT_SONG_POSITION = "Current Song Position";
    public static final String BROADCAST_FILTER = "On Song Completion";
    private static final String TAG = "PlayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Inside onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //register the LocalBroadcastManager instance
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(BROADCAST_FILTER));
        //retrieve the song position & song list from the intent extras
        songPosition = getIntent().getIntExtra(CURRENT_SONG_POSITION, 0);
        listOfSongs = getIntent().getParcelableArrayListExtra(SONG_LIST);
        //retrieve current song from the list using the position
        currentSong = listOfSongs.get(songPosition);
        //set the name of the current song
        currentSongTitle = (TextView)findViewById(R.id.song_name);
        currentSongTitle.setText(currentSong.getTitle());
        //set the artist name of the current song
        currentSongArtist = (TextView)findViewById(R.id.song_artist_name);
        currentSongArtist.setText(currentSong.getArtist());
        //initialise the media controls
        playButton = (ImageButton)findViewById(R.id.btnPlay);
        pauseButton = (ImageButton)findViewById(R.id.btnPause);
        prevButton = (ImageButton)findViewById(R.id.btnPrevious);
        nextButton = (ImageButton)findViewById(R.id.btnNext);
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        //handles intent received by broadcast sender
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Inside onReceive()");
            moveToNext();
            updateCurrentSongDetails();
            serviceIntent.setAction(MusicService.ACTION_START);
            startService(serviceIntent);
        }
    };

    /** Method of the Activity class **/
    @Override
    //handles functions of just starting the app
    protected void onStart(){
        if (serviceIntent == null){
            //initialise the service intent
            serviceIntent = new Intent(getApplicationContext(), MusicService.class);
            //create and start the service
            serviceIntent.setAction(MusicService.ACTION_START);
            startService(serviceIntent);
        }
        super.onStart();
    }

    public static Song getCurrentSong() {
        return currentSong;
    }

    @Override
    public void onClick(View button) {
        if(button == playButton){
//            Log.i(TAG, "Inside onClick play");
            serviceIntent.setAction(MusicService.ACTION_PLAY);
            startService(serviceIntent);
        }
        else if(button == pauseButton){
//            Log.i(TAG, "Inside onClick pause");
            serviceIntent.setAction(MusicService.ACTION_PAUSE);
            startService(serviceIntent);
        }
        else if(button == prevButton){
//            Log.i(TAG, "Inside onClick previous");
            moveToPrevious();
            updateCurrentSongDetails();
            serviceIntent.setAction(MusicService.ACTION_START);
            startService(serviceIntent);
        }
        else if(button == nextButton){
//            Log.i(TAG, "Inside onClick next");
            moveToNext();
            updateCurrentSongDetails();
            serviceIntent.setAction(MusicService.ACTION_START);
            startService(serviceIntent);
        }
    }

    private void updateCurrentSongDetails(){
        currentSong = listOfSongs.get(songPosition);
        currentSongTitle.setText(currentSong.getTitle());
        currentSongArtist.setText(currentSong.getArtist());
    }

    private void moveToPrevious(){
        songPosition--;
        if(songPosition < 0)
            songPosition = listOfSongs.size() - 1;
    }

    private void moveToNext(){
        songPosition++;
        if(songPosition >= listOfSongs.size())
            songPosition = 0;
    }

}
