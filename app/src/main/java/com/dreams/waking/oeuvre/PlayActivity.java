package com.dreams.waking.oeuvre;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{

    private ArrayList<Song> listOfSongs;
    private MusicService musicService;
    private Intent serviceIntent, playIntent;
    private int currentSongPosition;
    private static MusicController musicController;
    private Handler musicHandler = new Handler();
    private boolean musicBound = false;
    /** Initialising the service connection with the service class **/
    private ServiceConnection connectMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("PlayActivity", "Inside onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //retrieve the list of songs from the intent extras
        listOfSongs = getIntent().getParcelableArrayListExtra(SplashActivity.SONG_LIST);
        //retrieve the current song position from the intent extras
        currentSongPosition = getIntent().getIntExtra(MainActivity.SONG_POSITION,0);
        //set the name of the current song
        TextView currentSongTitle = (TextView)findViewById(R.id.song_name);
        currentSongTitle.setText(listOfSongs.get(currentSongPosition).getTitle());
        //set the artist name of the current song
        TextView currentSongArtist = (TextView)findViewById(R.id.song_artist_name);
        currentSongArtist.setText(listOfSongs.get(currentSongPosition).getArtist());
        //initialise the media controls
        setController();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("PlayActivity","Inside onPause()");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("PlayActivity","Inside onPause()");
    }

    @Override
    protected void onStop(){
        super.onStop();
        //unbindService(connectMusic);
        Log.i("PlayActivity","Inside onResume()");
    }

    /** Method of the Activity class **/
    @Override
    //handles termination of the app
    protected void onDestroy(){
        //getApplicationContext().unbindService(connectMusic);
        musicService = null;
        Log.i("PlayActivity","Inside onDestroy()");
        super.onDestroy();
    }

    /** Method of the Activity class **/
    @Override
    //handles functions of just starting the app
    protected void onStart(){
        Log.i("PlayActivity","Inside onStart()");
        connectMusic = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("PlayActivity","Service Connected");
                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
                musicService = binder.getService();
                musicBound = true;
                musicService.setSongList(listOfSongs);
                //retrieve current song position with the onClick event of the ListView
                musicService.setSong(currentSongPosition);
                musicService.setMusicController(musicController);
                //play the song set before
                musicService.playSong();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
        if (serviceIntent == null){
            serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, connectMusic, Context.BIND_AUTO_CREATE);
        }
        super.onStart();
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    //handles the start button
    public void start() {
        //Log.i("MediaControl","start()");
        musicService.startPlay();
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public void pause() {
        //Log.i("MediaControl","pause()");
        musicService.pausePlay();
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public int getDuration() {
        if (musicService != null && musicBound){
            return musicService.getSongDuration();
        }
        return 0;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicBound){
            return musicService.getSongPosition();
        }
        return 0;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public void seekTo(int pos) {
        musicService.seekPlay(pos);
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public boolean isPlaying() {
        if (musicService != null && musicBound){
            return musicService.isSongPlaying();
        }
        return false;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public int getBufferPercentage() {
        //return musicService.getBufferPercent();
        return 0;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public boolean canPause(){
        return true;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public boolean canSeekBackward() {
        return true;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public boolean canSeekForward() {
        return true;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    @Override
    public int getAudioSessionId() {
        return 0;
    }

    /** Method of the MediaController.MediaPlayerControl class **/
    private void setController(){
        musicController = new MusicController(this);
        //to handle previous & next button onClick events
        musicController.setPrevNextListeners(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playNext();
            }
        }, new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playPrevious();
            }
        });
        musicController.setMediaPlayer(this);
        //setting the ListView of songs as the amchor view for the MediaPlayerClontrol
        musicController.setAnchorView(findViewById(R.id.current_song));
        musicHandler.post(new Runnable() {
            public void run(){
                musicController.setEnabled(true);
                //musicController.show();
            }
        });
        //musicController.setEnabled(true);
        boolean showing = musicController.isShowing();
        //Log.i("Controls",""+showing);
    }

    /** Method to handle MediaPlayerControl Previous button **/
    private void playPrevious(){
        musicService.playPrevious();
    }

    /** Method to handle MediaPlayerControl Next button **/
    private void playNext(){
        musicService.playNext();
    }
}
