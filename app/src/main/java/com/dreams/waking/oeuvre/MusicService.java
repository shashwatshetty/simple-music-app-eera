package com.dreams.waking.oeuvre;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Shashwat on 5/31/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener{

    private MediaPlayer mediaPlayer;
    private SongRetriever songRetriever;
    private MusicController musicController;
    private ArrayList<Song> songList;
    private int songPosition;
    private Random randomize;
    private String songTitle = "";
    private boolean isOnShuffle = false;
    private final static int NOTIFICATION_ID = 1;
    private final IBinder bindMusic = new MusicBinder();

    public static final String ACTION_START = "Start Music";
    public static final String ACTION_PLAY = "Play Music";
    public static final String ACTION_PAUSE = "Pause Music";
    public static final String ACTION_NEXT = "Play Next";
    public static final String ACTION_PREVIOUS = "Play Previous";
    private static final String TAG = "MusicService";

    /** Method performs tasks when binding: MusicService to MainActivity **/
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return bindMusic;
        //return null;
    }

    /** Method performs tasks when unbinding: MusicService to MainActivity **/
    @Override
    public boolean onUnbind(Intent intent){
        /*mediaPlayer.stop();
        mediaPlayer.release();*/
        stopForeground(true);
        return true;
    }

    /** Method to handle playing a specific song from the ListView **/
    public void playSong(){
        mediaPlayer.reset();
        //retrieving the song and its details
        Song songToPlay = PlayActivity.getCurrentSong();
        //songTitle = songToPlay.getTitle();
        long songToPlayId = songToPlay.getId();
        //location of the specified song in the storage
        Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songToPlayId);
        try{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), songUri);
        }
        catch(Exception e){
            Log.e(TAG, "Error in Data Source Settings", e);
        }
        //inherent call to prepare()
        mediaPlayer.prepareAsync();
    }

    /** Method of Service class that handles tasks on creation **/
    @Override
    public void onCreate(){
        Log.i(TAG, "Service Started");
        super.onCreate();
        randomize = new Random();
        songRetriever = SongRetriever.getSongRetrieverInstance(getContentResolver());
        songRetriever.retrieveSongs();
        songList = songRetriever.getAllSongs();
        initMusicPlayer();
    }

    /** Method of Service class that handles tasks on destruction **/
    @Override
    public void onDestroy(){
        //musicController.
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        Log.i(TAG, "Inside onDestroy()");
        stopForeground(true);
    }

    /** Method that sets the shuffle flag **/
    public void setOnShuffle(){
        if (isOnShuffle)
            isOnShuffle = false;
        else
            isOnShuffle = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String intentAction = intent.getAction();
        switch(intentAction){
            case ACTION_START:
                playSong();
                break;
            case ACTION_PLAY:
                if(!isSongPlaying()){
                    startPlay();
                }
                break;
            case ACTION_PAUSE:
                if(isSongPlaying()) {
                    pausePlay();
                }
                break;
            case ACTION_NEXT:
                playNext();
                break;
        }
        return START_NOT_STICKY;
    }

    /** Method used to initialize the MediaPLayer **/
    public void initMusicPlayer(){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        }
        else{
            mediaPlayer.reset();
        }
    }

    /** Method used to initialize the songList **/
    public void setSongList(ArrayList<Song> allSongs){
        songList = allSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    /** Method of MediaPlayer.OnCompletionListener **/
    public void onCompletion(MediaPlayer mp){
        //when song is completed, the next song is played
        playNext();
    }

    public boolean onError(MediaPlayer mp, int what, int extra){
        return true;
    }

    public void onPrepared(MediaPlayer mp){
        Log.i(TAG,"onPrepared()");
        //starts the playback of the song
        mp.start();
        /*musicController.show(0);
        //creating a notification which will take me back to the MainActivity of the app
        Intent notificationIntent = new Intent(this, PlayActivity.class);
        notificationIntent.putExtra(SplashActivity.SONG_LIST,songList);
        notificationIntent.putExtra(MainActivity.SONG_POSITION,songPosition);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        //setting the contents in the notification
        notificationBuilder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.end).setTicker(songTitle).setOngoing(true).setContentTitle("Playing").setContentText(songTitle);
        Notification notification = notificationBuilder.build();
        startForeground(NOTIFICATION_ID, notification);*/
    }

    /** Setter for songPosition **/
    public void setSong(int songIndex){
        songPosition = songIndex;
    }

    /** Getter for songPosition **/
    public int getSongPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    /** Getter for songDuration to be shown in media control **/
    public int getSongDuration(){
        return mediaPlayer.getDuration();
    }

    /** Method to check if song is playing or no **/
    public boolean isSongPlaying(){
        return mediaPlayer.isPlaying();
    }

    /** Method that handles media control's pause button **/
    public void pausePlay(){
        mediaPlayer.pause();
    }

    /** Method that handles media control's seekBar **/
    public void seekPlay(int seekPosition){
        mediaPlayer.seekTo(seekPosition);
    }

    /** Method that handles media control's play button **/
    public void startPlay(){
        Log.i(TAG,"startPlay()");
        mediaPlayer.start();
    }

    /** Method that handles control's playPrevious button **/
    public void  playPrevious(){
        songPosition--;
        if (songPosition < 0){
            songPosition = songList.size() - 1;
        }
        playSong();
    }

    /** Method that handles control's playNext button **/
    public void playNext(){
        Log.i(TAG, "Inside playNext()");
        if (isOnShuffle){
            int newSongPosition = songPosition;
            while (newSongPosition == songPosition){
                newSongPosition = randomize.nextInt(songList.size());
            }
        }
        else{
            songPosition++;
            if (songPosition >= songList.size()){
                songPosition = 0;
            }
        }
        playSong();
    }

    public void setMusicController(MusicController controller){
        musicController = controller;
    }
}
