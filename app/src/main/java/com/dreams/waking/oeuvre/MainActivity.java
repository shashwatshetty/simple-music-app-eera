package com.dreams.waking.oeuvre;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private ArrayList<Song> songList;
    private ListView songView;
    private SongAdapter songAdapter;
    private SongRetriever songRetriever;
    public static final String SONG_POSITION = "Current Song Position";
    public static final String CURRENT_SONG = "Current Song Object";
    private static final String TAG = "MainActivity";

    /** Method of the Activity class **/
    @Override
    //handles the creation function of the app
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //instantiate the main activity layout
        setContentView(R.layout.activity_main);
        //retrieve list of songs from the view
        songView = (ListView)findViewById(R.id.songs_list);
        //extracting the songList populated in the SplashActivity
        songList = getIntent().getParcelableArrayListExtra(SplashActivity.SONG_LIST);
        //binding the adapter to the songList to render details of the songs on the screen
        songAdapter = new SongAdapter(this, songList);
        //setting the adapter for the ListView
        songView.setAdapter(songAdapter);
    }

    /** Method of the Activity class **/
    @Override
    //handles menu buttons
    public boolean onCreateOptionsMenu(Menu menu) {
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

    /** Method to handle ListView onClick events for the songs **/
    public void songPicked(View view){
        //retrieve the song position from the view tag
        int songPosition = Integer.parseInt(view.getTag().toString());
        //using the song position, retrieve song to be played
        Song songToPlay = songList.get(songPosition);
        //create intent and add the necessary data as extra
        Intent playerIntent = new Intent(MainActivity.this, PlayActivity.class);
        playerIntent.putExtra(SONG_POSITION, songPosition);
        playerIntent.putExtra(CURRENT_SONG, songToPlay);
        //start the intent
        startActivity(playerIntent);
    }
}
