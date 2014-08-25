package com.dtheng.playback.spela.model;

/**
 * author : Daniel Thengvall
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
     * The albums artwork
     */
    public Artwork artwork;

    /**
     * The length of the song in seconds
     */
    public int length;

    /**
     * Timestamp of when the track started playing
     */
    public long started;
}
