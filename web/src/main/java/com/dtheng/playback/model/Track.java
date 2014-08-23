package com.dtheng.playback.model;

/**
 * Created by danielthengvall on 8/22/14.
 */
public class Track {
    public int id;
    public String title;
    public String artist;
    public String album;
    public Artwork artwork;
    public int length;

    public CurrentTrack toCurrentTrack() {
        CurrentTrack newCurrent = new CurrentTrack();
        newCurrent.id = id;
        newCurrent.title = title;
        newCurrent.artist = artist;
        newCurrent.album = album;
        newCurrent.artwork = artwork;
        newCurrent.length = length;
        return newCurrent;
    }
}
