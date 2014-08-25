package com.dtheng.playback.model;

/**
 * @author Daniel Thengvall
 */
public class Track {

    /**
     * Numeric identifier
     */
    public int id;

    /**
     * The name of the song
     */
    public String title;

    /**
     * The artist of the song
     */
    public String artist;

    /**
     * The album the song appeared on
     */
    public String album;

    /**
     * Graphic assets
     */
    public Artwork artwork;

    /**
     * The length of the song in seconds
     */
    public int length;

    /**
     * Creates new current track object from this track
     * @return
     */
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
