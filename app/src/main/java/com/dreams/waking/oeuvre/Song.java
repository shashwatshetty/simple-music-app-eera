package com.dreams.waking.oeuvre;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Shashwat on 5/23/2016.
 */
/** Class implements Serializable to get packaged in intent **/
public class Song implements Parcelable {
    private long id;
    private String title;
    private String artist;

    public Song(long songId, String songTitle, String songArtist) {
        id=songId;
        title=songTitle;
        artist=songArtist;
    }

    public Song(Parcel in){
        id=in.readLong();
        title=in.readString();
        artist=in.readString();
    }
    public long getId(){
        return this.id;
    }
    public String getTitle(){
        return this.title;
    }
    public String getArtist() {
        return this.artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
