package com.klasha.assessment.security.filter;

import java.io.IOException;


import com.klasha.assessment.exception.EntityNotFoundException;
import com.klasha.assessment.exception.ErrorRes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.exceptions.JWTVerificationException;


public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ErrorRes errorRes= new ErrorRes();
        errorRes.setInstance(request.getRequestURI());
        response.setContentType("application/json");
        try {
            filterChain.doFilter(request, response);
        } catch (EntityNotFoundException e) {
            errorRes.setTitle("Username doesn't exist");
            errorRes.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorRes.setDetail(e.getMessage());
            errorRes.setType("Username doesn't exist");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        } catch (JWTVerificationException e) {
            errorRes.setTitle("JWT NOT VALID");
            errorRes.setStatus(HttpServletResponse.SC_FORBIDDEN);
            errorRes.setDetail(e.getMessage());
            errorRes.setType("JWT NOT VALID");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        } catch (RuntimeException e) {
            errorRes.setTitle("BAD REQUEST");
            errorRes.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorRes.setDetail(e.getMessage());
            errorRes.setType("BAD REQUEST");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        } catch (ServletException e) {
            errorRes.setTitle("Service not available");
            errorRes.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            errorRes.setDetail(e.getMessage());
            errorRes.setType("Service not available");
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        }
    }
}
