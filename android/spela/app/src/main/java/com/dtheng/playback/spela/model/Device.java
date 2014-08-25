package com.dtheng.playback.spela.model;

/**
 * author : Daniel Thengvall
 */
public class Device {

    /**
     * Numeric identifier
     */
    public int id;

    /**
     * The name of this device
     */
    public String name;

    /**
     * Is this device playing?
     */
    public boolean is_playing;

    @Override
    public String toString() {
        return name;
    }
}
