package com.dreams.waking.oeuvre;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Shashwat on 5/31/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener{

    private MediaPlayer mediaPlayer;
    private SongRetriever songRetriever;
    public static final String ACTION_START = "Start Music";
    public static final String ACTION_PLAY = "Play Music";
    public static final String ACTION_PAUSE = "Pause Music";
    public static final String ACTION_SEEK_BAR = "Seek Button";
    public static final String ACTION_SEEK_TO = "Seek Bar Progress";
    private static final String TAG = "MusicService";
    private static final String SEEK_POSITION = "Current Seek Postion";
    private static final String SEEK_DURATION = "Current Song Duration";

    /** Method performs tasks when binding: MusicService to MainActivity **/
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    /** Method of Service class that handles tasks on creation **/
    @Override
    public void onCreate(){
        super.onCreate();
        songRetriever = SongRetriever.getSongRetrieverInstance(getContentResolver());
        songRetriever.retrieveSongs();
        initMusicPlayer();
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
            case ACTION_SEEK_BAR:
                broadcastSeekData();
                break;
            case ACTION_SEEK_TO:
                seekPlay(intent.getIntExtra("SeekPosition",0));
                break;
        }
        return START_NOT_STICKY;
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
        }
        //inherent call to prepare()
        mediaPlayer.prepareAsync();
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

    /** Method of MediaPlayer.OnCompletionListener **/
    public void onCompletion(MediaPlayer mp){
        //when song is completed, the next song is played
        Intent broadcastIntent = new Intent(PlayActivity.BROADCAST_FILTER);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    public boolean onError(MediaPlayer mp, int what, int extra){
        return true;
    }

    public void onPrepared(MediaPlayer mp){
        //starts the playback of the song
        mp.start();
    }

    public void broadcastSeekData(){
        long current = getSongPosition();
        long duration = getSongDuration();
        Intent broadcastSeekIntent = new Intent(PlayActivity.SEEK_BROADCAST_FILTER);
        broadcastSeekIntent.putExtra(SEEK_POSITION,current);
        broadcastSeekIntent.putExtra(SEEK_DURATION,duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastSeekIntent);
    }

    /** Method that handles media control's pause button **/
    public void pausePlay(){
        mediaPlayer.pause();
    }

    /** Method that handles media control's play button **/
    public void startPlay(){
        mediaPlayer.start();
    }

    /** Method to check if song is playing or no **/
    public boolean isSongPlaying(){
        return mediaPlayer.isPlaying();
    }

    /** Getter for songPosition **/
    public int getSongPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    /** Getter for songDuration to be shown in media control **/
    public int getSongDuration(){
        return mediaPlayer.getDuration();
    }

    /** Method that handles media control's seekBar **/
    public void seekPlay(int seekPosition){
        mediaPlayer.seekTo(seekPosition);
    }
}
