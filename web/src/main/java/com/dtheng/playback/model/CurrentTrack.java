package com.dtheng.playback.model;

/**
 * @author Daniel Thengvall
 */
public class CurrentTrack extends Track {

    /**
     * When the track started playing
     */
    public long started;

    /**
     * Create current track object from this object
     * @return
     */
    public Track toTrack() {
        Track newTrack = new Track();
        newTrack.album = album;
        newTrack.artist = artist;
        newTrack.artwork = artwork;
        newTrack.id = id;
        newTrack.length = length;
        newTrack.title = title;
        return newTrack;
    }
}
