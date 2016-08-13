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

public class SearchActivity extends AppCompatActivity{
    private ArrayList<Song> searchResultList;
    ListView searchResultView;
    private TextView noResultsFound;
    SongAdapter songAdapter;
    public static final String CURRENT_SONG = "Current Song Object";
    private static final String NAME_OF_ACTIVITY = "SearchActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(NAME_OF_ACTIVITY,"Inside onCreate()");
        super.onCreate(savedInstanceState);
        //instantiate the search activity layout
        setContentView(R.layout.activity_search);
        //retrieve search intent
        Intent searchIntent = getIntent();
        //method call to handle the search intent query
        manageSearchIntent(searchIntent);
        //binding the adapter to the songList to render details of the songs on the screen
        songAdapter = new SongAdapter(this, searchResultList);
        //method call to handle displaying the search results
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
        //get the SearchManager instance from system services
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        //retrieve the SearchView from the menu bar
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        //code to remove the magnifying icon from the SearchView
        searchView.setIconifiedByDefault(false);
        int magId = this.getResources().getIdentifier("search_mag_icon", "id", "android");
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchView.setSearchableInfo(searchableInfo);
        return true;
    }

    /** Method of Activity class **/
    @Override
    //handles the search to  be directed to the same activity
    protected void onNewIntent(Intent intent) {
        //new search intent is set
        setIntent(intent);
        //method call to handle the search intent query
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

    /** Method to handle ListView onClick events for the songs **/
    public void songPicked(View view){
        //using the song position, retrieve song to be played
        Song songToPlay = searchResultList.get(Integer.parseInt(view.getTag().toString()));
        //create intent and add the necessary data as extra
        Intent playerIntent = new Intent(SearchActivity.this, PlayActivity.class);
        playerIntent.putExtra(CURRENT_SONG, songToPlay);
        //start the intent
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
