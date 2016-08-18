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
    private ListView searchResultView;
    private TextView noResultsFound;
    private SongAdapter songAdapter;
    private static final String SONG_LIST = "Song List";
    private static final String CURRENT_SONG_POSITION = "Current Song Position";
    private static final String TAG = "SearchActivity";

    private SongRetriever songRetriever;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"Inside onCreate()");
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
        Log.i(TAG,"Inside onCreateOptionsMenu()");
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
            //getSearchQueryList(new String[]{"%"+searchQuery+"%"});
            songRetriever = SongRetriever.getSongRetrieverInstance(getContentResolver());
            songRetriever.retrieveSongs(new String[]{"%"+searchQuery+"%"});
            searchResultList = songRetriever.getAllSearchSongs();
        }
    }

    /** Method to handle ListView onClick events for the songs **/
    public void songPicked(View view){
        //using the song position, retrieve song to be played
        int songPosition = Integer.parseInt(view.getTag().toString());
        //create intent and add the necessary data as extra
        Intent playerIntent = new Intent(SearchActivity.this, PlayActivity.class);
        //pack the song position & the song list
        playerIntent.putExtra(CURRENT_SONG_POSITION, songPosition);
        playerIntent.putParcelableArrayListExtra(SONG_LIST, searchResultList);
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
}
