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
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener/* MediaController.MediaPlayerControl*/{

    private static Song currentSong;
    private Intent serviceIntent;
    private int songPosition;
    private boolean musicPlaying = false;
    private ArrayList<Song> listOfSongs;
    private long seekPosition, seekDuration;
    private SeekBar songProgressBar;
    private Handler seekHandler = new Handler();
    private ConvertUtility utilObject = new ConvertUtility();
    private TextView currentSongTitle, currentSongArtist;
    private ImageButton playButton, pauseButton, prevButton, nextButton;
    private static final String SONG_LIST = "Song List";
    private static final String CURRENT_SONG_POSITION = "Current Song Position";
    public static final String BROADCAST_FILTER = "On Song Completion";
    public static final String SEEK_BROADCAST_FILTER = "Seekbar Update";
    private static final String TAG = "PlayActivity";
    private static final String SEEK_POSITION = "Current Seek Postion";
    private static final String SEEK_DURATION = "Current Song Duration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Inside onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //register the LocalBroadcastManager instance
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(BROADCAST_FILTER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mSeekReceiver, new IntentFilter(SEEK_BROADCAST_FILTER));
        //retrieve the song position & song list from the intent extras
        songPosition = getIntent().getIntExtra(CURRENT_SONG_POSITION, 0);
        listOfSongs = getIntent().getParcelableArrayListExtra(SONG_LIST);
        //retrieve the TextView from the layout
        currentSongTitle = (TextView)findViewById(R.id.song_name);
        currentSongArtist = (TextView)findViewById(R.id.song_artist_name);
        //update the song name and artist name
        updateCurrentSongDetails();
        //initialise the media controls
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        playButton = (ImageButton)findViewById(R.id.btnPlay);
        pauseButton = (ImageButton)findViewById(R.id.btnPause);
        prevButton = (ImageButton)findViewById(R.id.btnPrevious);
        nextButton = (ImageButton)findViewById(R.id.btnNext);
        songProgressBar.setOnSeekBarChangeListener(this);
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

    private BroadcastReceiver mSeekReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("PlayActivity.class","onReceive()");
            // Get extra data included in the Intent
            seekPosition = intent.getLongExtra(SEEK_POSITION,0);
            seekDuration = intent.getLongExtra(SEEK_DURATION,0);
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
            musicPlaying = true;
            startService(serviceIntent);
            updateProgressBar();
        }
        super.onStart();
    }

    public static Song getCurrentSong() {
        return currentSong;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        seekHandler.removeCallbacks(seekUpdate);
    }

    @Override
    public void onClick(View button) {
        if(button == playButton){
//            Log.i(TAG, "Inside onClick play");
            if(!musicPlaying) {
                musicPlaying = true;
                setMusicControlImages();
                serviceIntent.setAction(MusicService.ACTION_PLAY);
                startService(serviceIntent);
            }
        }
        else if(button == pauseButton){
//            Log.i(TAG, "Inside onClick pause");
            if(musicPlaying) {
                musicPlaying = false;
                setMusicControlImages();
                serviceIntent.setAction(MusicService.ACTION_PAUSE);
                startService(serviceIntent);
            }
        }
        else if(button == prevButton){
//            Log.i(TAG, "Inside onClick previous");
            musicPlaying = true;
            setMusicControlImages();
            moveToPrevious();
            updateCurrentSongDetails();
            serviceIntent.setAction(MusicService.ACTION_START);
            startService(serviceIntent);
        }
        else if(button == nextButton){
//            Log.i(TAG, "Inside onClick next");
            musicPlaying = true;
            setMusicControlImages();
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

    private void setMusicControlImages(){
        if(musicPlaying){
            playButton.setImageResource(R.drawable.play_btn_pressed);
            pauseButton.setImageResource(R.drawable.pause_btn);
        }
        else {
            playButton.setImageResource(R.drawable.play_btn);
            pauseButton.setImageResource(R.drawable.pause_btn_pressed);
        }
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

    public void updateProgressBar() {
        Log.i("PlayActivity.class","inside updateProgressBar()");
        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);
        seekHandler.postDelayed(seekUpdate, 100);
    }

    private Runnable seekUpdate = new Runnable() {
        public void run() {
            /*long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+totalDuration);
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));*/
            serviceIntent.setAction(MusicService.ACTION_SEEK_BAR);
            startService(serviceIntent);
            int progress;
            // Updating progress bar
            //Log.i("PlayActivity.class", "Duration: "+duration+" Position: "+position);
            if(seekDuration > 0){
                progress = utilObject.getProgressPercentage(seekPosition, seekDuration);
            }
            else{
                progress = 0;
            }
            //Log.d("Progress", ""+progress);
            //Log.i("PlayActivity.class", "Progress: "+progress+"%");
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            seekHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i("PlayActivity.class","inside onProgressChanged()");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i("PlayActivity.class","inside onStartTrackingTouch()");
        seekHandler.removeCallbacks(seekUpdate);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i("PlayActivity.class","inside onStopTrackingTouch()");
        seekHandler.removeCallbacks(seekUpdate);
        seekPosition = utilObject.progressToTimer(seekBar.getProgress(), (int)seekDuration);
        serviceIntent.setAction(MusicService.ACTION_SEEK_TO);
        serviceIntent.putExtra("SeekPosition", (int)seekPosition);
        startService(serviceIntent);
        updateProgressBar();
    }

}
