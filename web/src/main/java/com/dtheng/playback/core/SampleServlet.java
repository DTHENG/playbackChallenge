package com.dtheng.playback.core;

import com.dtheng.playback.util.WebUtil;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author Daniel Thengvall
*/
public class SampleServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(SampleServlet.class.getSimpleName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        // do something here

        // then redirect to the homepage
        res.sendRedirect(WebUtil.buildUrl(req, "/"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        // do stuff here

        // gets value from request
        WebUtil.setSessionAttribute("value", req.getParameter("value"), req);

        // then redirects to the homepage
        res.sendRedirect(WebUtil.buildUrl(req, "/"));
    }
}
