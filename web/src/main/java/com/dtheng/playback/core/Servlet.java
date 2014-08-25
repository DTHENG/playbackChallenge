package com.dtheng.playback.core;

import com.dtheng.playback.model.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author Daniel Thengvall
*/
public class Servlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(Servlet.class.getSimpleName());

    /**
     * Holds list of available tracks
     */
    private static final List<Track> availableTracks = new ArrayList<Track>();

    /**
     * Holds list of users
     */
    private static final Map<String, User> users = new HashMap<String, User>();

    /**
     * Processes info requests
     * @param req
     * @param res
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String user = req.getParameter("user");

        /**
         * Confirm user param and that they exist
         */
        if (user == null ||
                ! users.containsKey(user)) return;

        /**
         * Get users data
         */
        Response thisUsersData = users.get(user).response;
        switch (thisUsersData.state) {
            case PLAY:

                /**
                 * Is it time to play the next track?
                 */
                long now = new Date().getTime();
                long elapsed = now - thisUsersData.current.started;
                if ((int)(elapsed / 1000L) >= thisUsersData.current.length) {
                    if (thisUsersData.next == null) {
                        thisUsersData.position = 0;
                        thisUsersData.state = State.PAUSE;
                        break;
                    }
                    next(thisUsersData);
                }
        }

        /**
         * Update response timestamp
         */
        thisUsersData.current_time = new Date().getTime();

        /**
         * Build response
         */
        String json = new Gson().toJson(thisUsersData);
        int length = json.length();

        /**
         * Return json data
         */
        res.getWriter().write(json);
        res.setContentType("application/json");
        res.setContentLength(length);
    }

    /**
     * Processes authentication and update requests
     * @param req
     * @param res
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        /**
         * If no tracks are available, add them
         */
        if (availableTracks.size() == 0) setAvailableTracks();

        /**
         * Is this an authentication request?
         */
        if (req.getParameterMap().containsKey("auth")) {

            /**
             * Collect user data from request
             */
            String firstName = req.getParameter("first_name");
            String lastInitial = req.getParameter("last_initial");
            String deviceId = req.getParameter("device_id");
            String key = firstName + lastInitial;

            /**
             * Does user already exist?
             */
            if (users.containsKey(key)) {

                /**
                 * Is this device already known?
                 */
                Device newDevice = new Device();
                List<Device> existing = users.get(key).response.devices;
                for (int i = 0; i < existing.size(); i++) {
                    if (existing.get(i).name.equals(deviceId)) {
                        return;
                    }
                }

                /**
                 * Add the new deivce to users known devices list
                 */
                newDevice.id = existing.size();
                newDevice.is_playing = false;
                newDevice.name = deviceId;
                existing.add(newDevice);
                return;
            }

            /**
             * Setup new response object
             */
            Response newResponse = getNewResponseObject();

            /**
             * Add this device as first of known deivces
             */
            Device newDevice = new Device();
            newDevice.id = 0;

            /**
             * Set as default device currently playing
             */
            newDevice.is_playing = true;
            newDevice.name = deviceId;
            newResponse.devices.add(newDevice);

            /**
             * Build new user object
             */
            User newUser = new User();
            newUser.first_name = firstName;
            newUser.last_initial = lastInitial;
            newUser.response = newResponse;

            /**
             * Save new user
             */
            users.put(key, newUser);
            return;
        }

        /**
         * Is this an update request?
         */
        if (req.getParameterMap().containsKey("update")) {

            /**
             * Retrieve user id from request params
             */
            String user = req.getParameter("user");

            /**
             * Is this a known users?
             */
            if (user == null || ! users.containsKey(user)) return;

            /**
             * Get this users data out of memory
             */
            Response thisUsersData = users.get(user).response;

            /**
             * Update state, if present in request params
             */
            State state = ! req.getParameterMap().containsKey("state") ? thisUsersData.state : State.valueOf(req.getParameter("state"));

            /**
             * Update next, if preset in request params
             */
            boolean next = ! req.getParameterMap().containsKey("next") ? false : Boolean.parseBoolean(req.getParameter("next"));

            /**
             * Update previous, if preset in request params
             */
            boolean previous = ! req.getParameterMap().containsKey("previous") ? false : Boolean.parseBoolean(req.getParameter("previous"));

            /**
             * Update device id, if preset in request params
             */
            String device_id = ! req.getParameterMap().containsKey("device_id") ? "" : req.getParameter("device_id");

            /**
             * Take note of previous state
             */
            State previousState = thisUsersData.state;

            /**
             * Update state record
             */
            thisUsersData.state = state;

            switch (state) {
                case PLAY:

                    /**
                     * Don't make updates if state is the same
                     */
                    if (previousState == State.PLAY) break;

                    /**
                     * If starting track from paused state, calculate the time that the track "would have started" based on the saved position. Then make that the started time of the current track.
                     */
                    long elapsed = ((long) thisUsersData.position) * 1000L;
                    long historicalStart = new Date().getTime() - elapsed;
                    thisUsersData.current.started = historicalStart;
                    break;

                case PAUSE:

                    /**
                     * Don't make updates if state is the same
                     */
                    if (previousState == State.PAUSE) break;

                    /**
                     * If stoping track, calculate time elapsed, set that as the position record
                     */
                    long timeElapsed = new Date().getTime() - thisUsersData.current.started;
                    thisUsersData.position = (int)(timeElapsed / 1000L);
            }

            /**
             * Update playing device if preset in update request
             */
            if ( ! device_id.equals("")) {
                for (Device device : thisUsersData.devices) {
                    if (device.is_playing &&
                            !device.name.equals(device_id)) {
                        for (Device device2 : thisUsersData.devices) {
                            if (device2.name.equals(device_id)) {
                                device2.is_playing = true;
                                continue;
                            }
                            if (device2.is_playing) {
                                device2.is_playing = false;
                            }
                        }
                        break;
                    }
                }
            }

            /**
             * Switch to next track
             */
            if (next) {
                next(thisUsersData);
                return;
            }

            /**
             * Switch to previous track
             */
            if (previous) {

                /**
                 * Make previous track current track
                 */
                CurrentTrack newCurrentTrack = thisUsersData.previous.toCurrentTrack();

                /**
                 * Set new started time
                 */
                newCurrentTrack.started = new Date().getTime();

                /**
                 * Make current track next track
                 */
                Track newNextTrack = thisUsersData.current.toTrack();
                if (newCurrentTrack.id - 1 >= 0) {
                    /**
                     * Save this users response record with new previous track
                     */
                    Track newPreviousTrack = availableTracks.get(newCurrentTrack.id -1);
                    thisUsersData.previous = newPreviousTrack;
                } else {
                    /**
                     * Set this users response record to have no previous track, since none exists
                     */
                    thisUsersData.previous = null;
                }

                /**
                 * Save users response record with new current and next tracks
                 */
                thisUsersData.current = newCurrentTrack;
                thisUsersData.next = newNextTrack;
            }
        }
    }

    /**
     * Determines next track based on a users response object
     * @param data
     */
    private static final void next(Response data) {

        /**
         * Make next track new current track
         */
        CurrentTrack newCurrentTrack = data.next.toCurrentTrack();

        /**
         * Set new started time
         */
        newCurrentTrack.started = new Date().getTime();
        if (availableTracks.size() > newCurrentTrack.id +1) {

            /**
             * Set new next track from list of available tracks
             */
            Track newNextTrack = availableTracks.get(newCurrentTrack.id +1);
            data.next = newNextTrack;
        } else {

            /**
             * Set next track to null since there is none
             */
            data.next = null;
        }

        /**
         * Set current track to be previous
         */
        Track newPreviousTrack = data.current;

        /**
         * Save new previous and current tracks
         */
        data.previous = newPreviousTrack;
        data.current = newCurrentTrack;
    }

    /**
     * Builds default response object
     * @return
     */
    private static final Response getNewResponseObject() {
        Response response = new Response();
        response.previous = availableTracks.get(0);
        response.current = availableTracks.get(1).toCurrentTrack();
        response.current.started = new Date().getTime();
        response.next = availableTracks.get(2);
        response.position = 0;
        response.devices = new ArrayList<Device>();
        response.state = State.PAUSE;
        return response;
    }

    /**
     * Defines what tracks are available
     */
    private static final void setAvailableTracks() {

        Track blackSugar = new Track();
        blackSugar.album = "Sticky Fingers";
        blackSugar.artist = "The Rolling Stones";
        blackSugar.title = "Brown Sugar";
        blackSugar.length = 229;
        blackSugar.id = 0;

        Track paintItBlack = new Track();
        paintItBlack.album = "AFTERMATH";
        paintItBlack.artist = "The Rolling Stones";
        paintItBlack.title = "Paint It, Black";
        paintItBlack.length = 202;
        paintItBlack.id = 1;

        Track sympathyForTheDevil = new Track();
        sympathyForTheDevil.album = "BEGGARS BANQUET";
        sympathyForTheDevil.artist = "The Rolling Stones";
        sympathyForTheDevil.title = "Sympathy For The Devil";
        sympathyForTheDevil.length = 378;
        sympathyForTheDevil.id = 2;

        availableTracks.add(blackSugar);
        availableTracks.add(paintItBlack);
        availableTracks.add(sympathyForTheDevil);

    }
}
