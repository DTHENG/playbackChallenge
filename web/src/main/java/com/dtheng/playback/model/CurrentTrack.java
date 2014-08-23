package com.dtheng.playback.model;

/**
 * Created by danielthengvall on 8/23/14.
 */
public class CurrentTrack extends Track {
    public long started;

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
