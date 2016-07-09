package com.dreams.waking.oeuvre;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by Shashwat on 5/24/2016.
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songList;
    private LayoutInflater songInfo;

    public SongAdapter(Context context, ArrayList<Song> songList){
        //Log.i("SongAdapter","Inside SongAdapter()");
        this.songList = songList;
        songInfo = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Log.i("SongAdapter","songList size is: "+this.songList.size());
    }

    /** BaseAdapter method to return the size of the ListView **/
    @Override
    public int getCount(){
        return this.songList.size();
    }

    /** BaseAdapter method **/
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /** BaseAdapter method **/
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /** Method used to populate the ListView on scrolling **/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.i("SongAdapter","List Size: "+getCount());
        //ViewHolder viewHolder;
        if (convertView == null) {
            convertView = songInfo.inflate(R.layout.song, parent, false);
            /*viewHolder = new ViewHolder();
            viewHolder.songImage = (ImageView)convertView.findViewById(R.id.song_image);
            viewHolder.songTitle = (TextView)convertView.findViewById(R.id.song_title);
            viewHolder.songArtist = (TextView)convertView.findViewById(R.id.song_artist);*/
        }
        Song song = songList.get(position);
        /*viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.songImage.setImageResource(R.drawable.grammy);
        viewHolder.songTitle.setText(song.getTitle());
        viewHolder.songArtist.setText(song.getArtist());*/
        ImageView songImageView = (ImageView)convertView.findViewById(R.id.song_image);
        TextView songTitleView = (TextView)convertView.findViewById(R.id.song_title);
        TextView songArtistView = (TextView)convertView.findViewById(R.id.song_artist);
        songImageView.setImageResource(R.drawable.grammy);
        songTitleView.setText(song.getTitle());
        songArtistView.setText(song.getArtist());
        convertView.setTag(position);
        return convertView;
    }

    /*static class ViewHolder{
        ImageView songImage;
        TextView songTitle;
        TextView songArtist;
        int position;
        public int getPosition(){
            return position;
        }
    }*/

}
