package com.dtheng.playback.util;

import com.google.api.client.http.GenericUrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebUtil {

    public static String buildUrl(HttpServletRequest req, String relativePath) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath(relativePath);
        return url.build();
    }

    public static String getSessionAttribute(String attr, HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute(attr);
    }

    public static void setSessionAttribute(String attr, String value, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(attr, value);
    }
}
