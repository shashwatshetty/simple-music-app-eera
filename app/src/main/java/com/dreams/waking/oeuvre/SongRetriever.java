package com.dreams.waking.oeuvre;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Shashwat on 7/20/2016.
 */
public class SongRetriever {
    private ContentResolver contentResolver;
    private static ArrayList<Song> allSongs;
    private static SongRetriever singleInstance;

    private SongRetriever(ContentResolver cResolver){
        contentResolver = cResolver;
        allSongs = new ArrayList<Song>();
    }

    public static SongRetriever getSongRetrieverInstance(ContentResolver cResolver) {
        singleInstance = new SongRetriever(cResolver);
        return singleInstance;
    }

    public void retrieveSongs(){
        //building the uri where the audio files will be searched for in the storage
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //allowing read/write access to the specified location in the uri to retrieve audio files
        Cursor musicCursor = contentResolver.query(musicUri, null, "IS_MUSIC != 0", null, "TITLE");
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
                allSongs.add(new Song(songId, songTitle, songArtist));
            }while(musicCursor.moveToNext());
        }
    }

    public ArrayList<Song> getAllSongs() {
        return allSongs;
    }
}
