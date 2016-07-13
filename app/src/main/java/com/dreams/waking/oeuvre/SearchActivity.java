package com.dreams.waking.oeuvre;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity /*implements MediaController.MediaPlayerControl*/{
    private ArrayList<Song> searchResultList;
    private MusicService musicService;
    private Intent playIntent;
    private static MusicController musicController;
    ListView searchResultView;
    private TextView noResultsFound;
    SongAdapter songAdapter;
    private Handler musicHandler = new Handler();
    private boolean musicBound = false;
    private static final String NAME_OF_ACTIVITY = "SearchActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(NAME_OF_ACTIVITY,"Inside onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent searchIntent = getIntent();
        manageSearchIntent(searchIntent);
        songAdapter = new SongAdapter(this, searchResultList);
        displaySearchResults();
    }

    /** Method of the Activity class **/
    @Override
    //handles menu buttons
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(NAME_OF_ACTIVITY,"Inside onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        //instantiating the menu layout to the menu inflater
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        int magId = this.getResources().getIdentifier("search_mag_icon", "id", "android");
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchView.setSearchableInfo(searchableInfo);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(NAME_OF_ACTIVITY, "Inside onNewIntent");
        setIntent(intent);
        manageSearchIntent(intent);
        songAdapter.setSongList(searchResultList);
        displaySearchResults();
    }

    private void manageSearchIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            getSearchQueryList(new String[]{"%"+searchQuery+"%"});
        }
    }

    /** Method of the Activity class **/
    @Override
    //handles termination of the app
    protected void onDestroy(){
        //stopService(playIntent);
        //getApplicationContext().unbindService(connectMusic);
        //musicService = null;
        Log.i(NAME_OF_ACTIVITY,"Inside onDestroy()");
        super.onDestroy();
    }

    /** Method of the Activity class **/
    @Override
    //handles functions of just starting the app
    protected void onStart(){
        Log.i(NAME_OF_ACTIVITY,"Inside onStart()");
        super.onStart();
        /*if (playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, connectMusic, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }*/
    }

    @Override
    protected void onResume(){
        Log.i(NAME_OF_ACTIVITY,"Inside onResume()");
        super.onResume();
    }

    @Override
    protected void onStop(){
        Log.i(NAME_OF_ACTIVITY,"Inside onStop()");
        super.onStop();
    }

    @Override
    protected void onPause(){
        Log.i(NAME_OF_ACTIVITY,"Inside onPause()");
        super.onPause();
    }

//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    //handles the start button
//    public void start() {
//        //Log.i("MediaControl","start()");
//        musicService.startPlay();
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public void pause() {
//        //Log.i("MediaControl","pause()");
//        musicService.pausePlay();
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public int getDuration() {
//        if (musicService != null && musicBound){
//            return musicService.getSongDuration();
//        }
//        return 0;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public int getCurrentPosition() {
//        if (musicService != null && musicBound){
//            return musicService.getSongPosition();
//        }
//        return 0;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public void seekTo(int pos) {
//        musicService.seekPlay(pos);
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public boolean isPlaying() {
//        if (musicService != null && musicBound){
//            return musicService.isSongPlaying();
//        }
//        return false;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public int getBufferPercentage() {
//        //return musicService.getBufferPercent();
//        return 0;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public boolean canPause(){
//        return true;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public boolean canSeekBackward() {
//        return true;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public boolean canSeekForward() {
//        return true;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    @Override
//    public int getAudioSessionId() {
//        return 0;
//    }
//
//    /** Method of the MediaController.MediaPlayerControl class **/
//    private void setController(){
//        musicController = new MusicController(this);
//        //to handle previous & next button onClick events
//        musicController.setPrevNextListeners(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                playNext();
//            }
//        }, new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                playPrevious();
//            }
//        });
//        musicController.setMediaPlayer(this);
//        //setting the ListView of songs as the amchor view for the MediaPlayerClontrol
//        musicController.setAnchorView(findViewById(R.id.songs_list));
//        musicHandler.post(new Runnable() {
//            public void run(){
//                musicController.setEnabled(true);
//                //musicController.show();
//            }
//        });
//        //musicController.setEnabled(true);
//        boolean showing = musicController.isShowing();
//        //Log.i("Controls",""+showing);
//    }
//
//    /** Method to handle MediaPlayerControl Previous button **/
//    private void playPrevious(){
//        musicService.playPrevious();
//    }
//
//    /** Method to handle MediaPlayerControl Next button **/
//    private void playNext(){
//        musicService.playNext();
//    }
//
//    /** Initialising the service connection with the service class **/
//    private ServiceConnection connectMusic = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.i("SearchActivity","Service Connected");
//            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
//            musicService = binder.getService();
//            musicService.setSongList(searchResultList);
//            musicBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            musicBound = false;
//        }
//    };

    /** Method to handle ListView onClick events for the songs **/
    public void songPicked(View view){
        int songToPlay = Integer.parseInt(view.getTag().toString());
        Intent playerIntent = new Intent(SearchActivity.this, PlayActivity.class);
        playerIntent.putExtra(MainActivity.SONG_POSITION, songToPlay);
        playerIntent.putExtra(SplashActivity.SONG_LIST, searchResultList);
        startActivity(playerIntent);
    }

    /** Method to display the Search results **/
    public void displaySearchResults(){
        noResultsFound = (TextView)findViewById(R.id.no_results_found);
        if (searchResultList.isEmpty()) {
            noResultsFound.setText("No Results Found!!");
        }
        else{
            noResultsFound.setText("");
            //retrieve the ListView from the layout file
            searchResultView = (ListView) findViewById(R.id.search_result);
            //initialise the Adapter and set it to the ListView
            searchResultView.setAdapter(songAdapter);
            //initialising the media player controls
            //setController();
        }
        songAdapter.setSongList(searchResultList);
    }

    /** Method to retrieve the lis of songs from the internal storage **/
    public void getSearchQueryList(String[] argument){
        Log.i("SearchActivity", "Inside getSongList()");
        searchResultList = new ArrayList<Song>();
        //ContentResolver to interact with the audio content provider
        ContentResolver musicResolver = getContentResolver();
        //building the uri where the audio files will be searched for in the storage
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //allowing read/write access to the specified location in the uri to retrieve audio files
        Cursor musicCursor = musicResolver.query(musicUri, null, "IS_MUSIC != 0 AND TITLE LIKE ?", argument, "TITLE");
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
                searchResultList.add(new Song(songId, songTitle, songArtist));
            }while(musicCursor.moveToNext());
        }
        Log.i("SearchActivity", "Search Query Size: "+searchResultList.size());
    }
}
