package com.dtheng.playback.spela.model;

import java.util.List;

/**
 * Created by danielthengvall on 8/22/14.
 */
public class Response {
    public Track previous;
    public Track current;
    public Track next;
    public State state;
    public int position;
    public List<Device> devices;
}
