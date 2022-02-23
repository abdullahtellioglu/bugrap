package com.vaadin.bugrap.utils;

import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {
    private static final String COOKIE_PROJECT_VERSION_PREFIX = "project_%d";
    private static final String USER_NAME = "user_name";
    private CookieUtils(){

    }
    public static void addLastSelectedProjectVersion(Project project, ProjectVersion projectVersion, HttpServletResponse response){
        if(project == null){
            return;
        }
        if(projectVersion == null){
            return;
        }
        if(response == null){
            return;
        }

        Cookie cookie = new Cookie(String.format(COOKIE_PROJECT_VERSION_PREFIX, project.getId()), String.valueOf(projectVersion.getId()));
        response.addCookie(cookie);
    }
    public static int getLastSelectedProjectVersion(Project project, HttpServletRequest request){
        if(project == null){
            return -1;
        }
        if(request == null){
            return -1;
        }
        Cookie[] cookies = request.getCookies();
        String hashId = String.format(COOKIE_PROJECT_VERSION_PREFIX, project.getId());
        Optional<Cookie> foundCookie = Arrays.stream(cookies).filter(f -> f.getName().equals(hashId)).findFirst();
        return foundCookie.map(cookie -> Integer.parseInt(cookie.getValue())).orElse(-1);
    }
    public static String getCurrentUserName(HttpServletRequest request){
        if(request == null){
            return null;
        }
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(USER_NAME)).findFirst();
        return first.map(Cookie::getValue).orElse(null);
    }
    public static void putCurrentUserName(String name, HttpServletResponse response){
        if(response == null){
            return;
        }
        Cookie cookie = new Cookie(USER_NAME, name);
        response.addCookie(cookie);
    }
}
