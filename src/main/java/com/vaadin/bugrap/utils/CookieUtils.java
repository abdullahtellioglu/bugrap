package com.vaadin.bugrap.utils;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {
    private static final String COOKIE_PROJECT_VERSION_PREFIX = "project_%d";
    private static final String USER_NAME = "user_name";

    private CookieUtils() {

    }

    public static void addLastSelectedProjectVersion(Project project, ProjectVersion projectVersion) {
        if (project == null) {
            return;
        }
        if (projectVersion == null) {
            return;
        }
        VaadinResponse response = VaadinResponse.getCurrent();
        if (response == null) {
            return;
        }
        Cookie cookie = new Cookie(String.format(COOKIE_PROJECT_VERSION_PREFIX, project.getId()), String.valueOf(projectVersion.getId()));
        response.addCookie(cookie);
    }

    public static int getLastSelectedProjectVersion(Project project) {
        if (project == null) {
            return -1;
        }
        VaadinRequest request = VaadinRequest.getCurrent();
        if (request == null) {
            return -1;
        }

        Cookie[] cookies = request.getCookies();
        String hashId = String.format(COOKIE_PROJECT_VERSION_PREFIX, project.getId());
        Optional<Cookie> foundCookie = Arrays.stream(cookies).filter(f -> f.getName().equals(hashId)).findFirst();
        return foundCookie.map(cookie -> Integer.parseInt(cookie.getValue())).orElse(-1);
    }

    public static String getCurrentUserName() {
        VaadinRequest request = VaadinRequest.getCurrent();
        if (request == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(USER_NAME)).findFirst();
        return first.map(Cookie::getValue).orElse(null);
    }

    public static void putCurrentUserName(String name) {
        VaadinResponse response = VaadinResponse.getCurrent();
        if (response == null) {
            return;
        }
        Cookie cookie = new Cookie(USER_NAME, name);
        response.addCookie(cookie);
    }
}
