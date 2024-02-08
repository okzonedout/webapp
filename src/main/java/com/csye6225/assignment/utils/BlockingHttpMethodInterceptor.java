package com.csye6225.assignment.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.csye6225.assignment.Constants.HEALTH_URI;
import static com.csye6225.assignment.Constants.V1_USER_URI;

public class BlockingHttpMethodInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate;");

        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (requestURI.equalsIgnoreCase(HEALTH_URI)){
            if (HttpMethod.GET.matches(requestMethod)){
                return true;
            }
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value());
            response.setContentLength(0);
            return false;

        } else if (requestURI.equalsIgnoreCase(V1_USER_URI) ){
            if((HttpMethod.POST.matches(requestMethod) || HttpMethod.PUT.matches(requestMethod) || HttpMethod.GET.matches(requestMethod))){
                return true;
            }
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value());
            response.setContentLength(0);
            return false;
        }
//        response.setContentLength(0);
        return true;
    }
}
