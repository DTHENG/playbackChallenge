package com.dtheng.playback.core;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Thengvall
 */
public class SampleFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(SampleFilter.class.getSimpleName());

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)  throws IOException, ServletException {

        if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (httpRequest.getRequestURI().startsWith("/static")) {
                filterChain.doFilter(request, response);
                return;
            }

            // do something here

            filterChain.doFilter(request, response);
        } else {
            LOG.warning("Unexpected non HTTP servlet response. Proceeding anyway.");
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
}
