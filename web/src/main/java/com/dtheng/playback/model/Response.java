package com.dtheng.playback.model;

import java.util.List;

/**
 * @author Daniel Thengvall
 */
public class Response {

    /**
     * The track that was played last
     */
    public Track previous;

    /**
     * The track currently playing or paused
     */
    public CurrentTrack current;

    /**
     * The next track in the play queue
     */
    public Track next;

    /**
     * The current state of the player
     */
    public State state;

    /**
     * The point in the track when the player was paused
     */
    public int position;

    /**
     * List of known devices available for playback and control
     */
    public List<Device> devices;

    /**
     * The time this response was generated
     */
    public long current_time;
}
