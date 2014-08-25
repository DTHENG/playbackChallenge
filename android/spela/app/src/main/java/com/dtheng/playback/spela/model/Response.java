package com.dtheng.playback.spela.model;

import java.util.List;

/**
 * author : Daniel Thengvall
 */
public class Response {

    /**
     * The track played previously
     */
    public Track previous;

    /**
     * The track currently playing or paused
     */
    public Track current;

    /**
     * The next track in the play queue
     */
    public Track next;

    /**
     * The state of the player, PLAY or PAUSE
     */
    public State state;

    /**
     * The position of the track when paused
     */
    public int position;

    /**
     * Known devices
     */
    public List<Device> devices;

    /**
     * Response timestamp
     */
    public long current_time;
}
