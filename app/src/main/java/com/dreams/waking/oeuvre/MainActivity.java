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
    private static final String NAME_OF_ACTIVITY = "MainActivity";

    /** Method of the Activity class **/
    @Override
    //handles the creation function of the app
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(NAME_OF_ACTIVITY, "Inside onCreate()");
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
    protected void onPause(){
        super.onPause();
        Log.i(NAME_OF_ACTIVITY,"Inside onPause()");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(NAME_OF_ACTIVITY,"Inside onStop()");
    }

    /** Method of the Activity class **/
    @Override
    //handles termination of the app
    protected void onDestroy(){
        super.onDestroy();
        Log.i(NAME_OF_ACTIVITY,"Inside onDestroy()");
    }

    /** Method of the Activity class **/
    @Override
    //handles functions of just starting the app
    protected void onStart(){
        super.onStart();
        Log.i(NAME_OF_ACTIVITY,"Inside onStart");
    }

    @Override
    protected void onResume(){
        Log.i(NAME_OF_ACTIVITY, "Inside onResume()");
        super.onResume();
    }

    /** Method to handle ListView onClick events for the songs **/
    public void songPicked(View view){
        int songToPlay = Integer.parseInt(view.getTag().toString());
        Intent playerIntent = new Intent(MainActivity.this, PlayActivity.class);
        playerIntent.putExtra(SONG_POSITION, songToPlay);
        playerIntent.putExtra(SplashActivity.SONG_LIST, songList);
        startActivity(playerIntent);
    }
}
