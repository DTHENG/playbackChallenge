package com.dtheng.playback.spela.model;

/**
 * Created by danielthengvall on 8/22/14.
 */
public class Device {
    public int id;
    public String name;
    public boolean is_playing;

    @Override
    public String toString() {
        return name;
    }
}
