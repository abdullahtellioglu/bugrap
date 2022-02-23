package com.vaadin.bugrap.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestUtils {
    private RequestUtils(){

    }
    public static HttpServletRequest getCurrentHttpRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request;
        }

        return null;
    }

    public static HttpServletResponse getCurrentHttpResponse(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(servletRequestAttributes != null){
            return servletRequestAttributes.getResponse();
        }

        return null;
    }
}
