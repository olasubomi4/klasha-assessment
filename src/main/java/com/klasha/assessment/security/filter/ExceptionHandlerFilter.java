package com.klasha.assessment.security.filter;

import java.io.IOException;
import java.util.Arrays;

import com.klasha.assessment.exception.EntityNotFoundException;
import com.klasha.assessment.exception.ErrorResponse;
import com.klasha.assessment.exception.InvalidCredentialsException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.exceptions.JWTVerificationException;


public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws IOException {
        ErrorResponse errorRes= new ErrorResponse();
        response.setContentType("application/json");
        try {
            filterChain.doFilter(request, response);
        } catch (EntityNotFoundException e) {
            errorRes.setMessage("Username doesn't exist");
            errorRes.setErrors(Arrays.asList(e.getMessage()));
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        } catch (JWTVerificationException e) {
            errorRes.setMessage("JWT NOT VALID");
            errorRes.setErrors(Arrays.asList(e.getMessage()));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        } catch (InvalidCredentialsException e) {
            errorRes.setMessage("Unauthorized");
            errorRes.setErrors(Arrays.asList(e.getMessage()));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        } catch (RuntimeException e) {
            errorRes.setMessage("BAD REQUEST");
            errorRes.setErrors(Arrays.asList(e.getMessage()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        } catch (ServletException e) {
            errorRes.setMessage("Service not available");
            errorRes.setErrors(Arrays.asList(e.getMessage()));
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write(errorRes.toString());
            response.getWriter().flush();
        }
    }
}
