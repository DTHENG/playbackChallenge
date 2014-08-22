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

            String json =
                    new Gson().toJson(
                            users.get(user).response);
            int length = json.length();
            res.getWriter().write(json);
            res.setContentType("application/json");
            res.setContentLength(length);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        if (availableTracks.size() == 0) setAvailableTracks();

        if (req.getParameterMap().containsKey("auth")) {

            String firstName = req.getParameter("first_name");
            String lastInitial = req.getParameter("last_initial");
            String deviceId = req.getParameter("device_id");

            LOG.info("processing auth request from "+ firstName +" "+ lastInitial);

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

            LOG.info("processing update request from "+ user);

            if (user != null &&
                    users.containsKey(user)) {

                Response thisUsersData = users.get(user).response;
                thisUsersData.state = state;

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
                    Track newCurrentTrack = thisUsersData.next;
                    if (availableTracks.size() > newCurrentTrack.id +1) {
                        Track newNextTrack = availableTracks.get(newCurrentTrack.id +1);
                        thisUsersData.next = newNextTrack;
                    } else {
                        thisUsersData.next = null;
                    }
                    Track newPreviousTrack = thisUsersData.current;
                    thisUsersData.previous = newPreviousTrack;
                    thisUsersData.current = newCurrentTrack;
                    return;
                }

                if (previous) {
                    Track newCurrentTrack = thisUsersData.previous;
                    Track newNextTrack = thisUsersData.current;
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

    private static final Response getNewResponseObject() {
        Response response = new Response();
        response.previous = availableTracks.get(0);
        response.current = availableTracks.get(1);
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
