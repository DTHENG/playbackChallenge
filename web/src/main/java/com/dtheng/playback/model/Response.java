package com.dtheng.playback.model;

import java.util.List;

/**
 * Created by danielthengvall on 8/22/14.
 */
public class Response {
    public Track previous;
    public CurrentTrack current;
    public Track next;
    public State state;
    public int position;
    public List<Device> devices;
    public long current_time;
}
