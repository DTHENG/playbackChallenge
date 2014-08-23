package com.dtheng.playback.core;

import com.dtheng.playback.model.*;
import com.dtheng.playback.util.WebUtil;
import com.google.api.client.util.ArrayMap;
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

    private static final List<Track> availableTracks = new ArrayList<Track>();

    private static final Map<String, User> users = new HashMap<String, User>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String user = req.getParameter("user");

        if (user != null &&
                users.containsKey(user)) {

            LOG.info("processing get request from "+ user);

            Response thisUsersData = users.get(user).response;

            switch (thisUsersData.state) {
                case PLAY:
                    long now = new Date().getTime();
                    long elapsed = now - thisUsersData.current.started;
                    if ((int)(elapsed / 1000L) >= thisUsersData.current.length) {
                        next(thisUsersData);
                        if (thisUsersData.next == null) {
                            thisUsersData.state = State.PAUSE;
                        }
                    }
            }

            thisUsersData.current_time = new Date().getTime();

            String json =
                    new Gson().toJson(thisUsersData);
            int length = json.length();
            res.getWriter().write(json);
            res.setContentType("application/json");
            res.setContentLength(length);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        if (availableTracks.size() == 0) setAvailableTracks();

        LOG.info("post request made");

        if (req.getParameterMap().containsKey("auth")) {

            String firstName = req.getParameter("first_name");
            String lastInitial = req.getParameter("last_initial");
            String deviceId = req.getParameter("device_id");

            LOG.info("processing auth request from "+ firstName +" "+ lastInitial +" - "+ deviceId);

            String key = firstName + lastInitial;

            if (users.containsKey(key)) {

                Device newDevice = new Device();
                List<Device> existing = users.get(key).response.devices;
                for (int i = 0; i < existing.size(); i++) {
                    if (existing.get(i).name.equals(deviceId)) {
                        return;
                    }
                }
                newDevice.id = existing.size();
                newDevice.is_playing = false;
                newDevice.name = deviceId;
                existing.add(newDevice);
                return;
            }

            Response newResponse = getNewResponseObject();
            Device newDevice = new Device();
            newDevice.id = 0;
            newDevice.is_playing = true;
            newDevice.name = deviceId;
            newResponse.devices.add(newDevice);
            User newUser = new User();
            newUser.first_name = firstName;
            newUser.last_initial = lastInitial;
            newUser.response = newResponse;
            users.put(key, newUser);
            return;
        }


        if (req.getParameterMap().containsKey("update")) {

            String user = req.getParameter("user");
            State state = State.valueOf(req.getParameter("state"));
            boolean next = Boolean.parseBoolean(req.getParameter("next"));
            boolean previous = Boolean.parseBoolean(req.getParameter("previous"));
            String device_id = req.getParameter("device_id");

            LOG.info("processing update request from "+ user +" - "+ device_id);

            if (user != null &&
                    users.containsKey(user)) {

                Response thisUsersData = users.get(user).response;
                State previousState = thisUsersData.state;
                thisUsersData.state = state;

                switch (state) {
                    case PLAY:
                        if (previousState == State.PLAY) break;
                        long elapsed = ((long) thisUsersData.position) * 1000L;
                        long historicalStart = new Date().getTime() - elapsed;
                        thisUsersData.current.started = historicalStart;
                        break;
                    case PAUSE:
                        if (previousState == State.PAUSE) break;
                        long timeElapsed = new Date().getTime() - thisUsersData.current.started;
                        thisUsersData.position = (int)(timeElapsed / 1000L);
                }

                for (Device device : thisUsersData.devices) {
                    if (device.is_playing &&
                            ! device.name.equals(device_id)) {
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

                if (next) {
                    next(thisUsersData);
                    return;
                }

                if (previous) {
                    CurrentTrack newCurrentTrack = thisUsersData.previous.toCurrentTrack();
                    newCurrentTrack.started = new Date().getTime();
                    Track newNextTrack = thisUsersData.current.toTrack();
                    if (newCurrentTrack.id - 1 >= 0) {
                        Track newPreviousTrack = availableTracks.get(newCurrentTrack.id -1);
                        thisUsersData.previous = newPreviousTrack;
                    } else {
                        thisUsersData.previous = null;
                    }
                    thisUsersData.current = newCurrentTrack;
                    thisUsersData.next = newNextTrack;
                }
            }
        }
    }

    private static final void next(Response data) {
        CurrentTrack newCurrentTrack = data.next.toCurrentTrack();
        newCurrentTrack.started = new Date().getTime();
        if (availableTracks.size() > newCurrentTrack.id +1) {
            Track newNextTrack = availableTracks.get(newCurrentTrack.id +1);
            data.next = newNextTrack;
        } else {
            data.next = null;
        }
        Track newPreviousTrack = data.current;
        data.previous = newPreviousTrack;
        data.current = newCurrentTrack;
    }

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

    private static final void setAvailableTracks() {

        Track blackSugar = new Track();
        blackSugar.album = "Sticky Fingers";
        blackSugar.artist = "The Rolling Stones";
        blackSugar.title = "Black Sugar";
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
