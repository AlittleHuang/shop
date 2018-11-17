package com.shengchuang.config.springmvc;

import org.springframework.context.annotation.Lazy;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Lazy
@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Map<String, HttpSession> map = new ConcurrentHashMap<>();

    public static final HttpSession getSession(String id) {
        return map.get(id);
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        map.put(session.getId(), session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        map.remove(httpSessionEvent.getSession().getId());
    }
}
