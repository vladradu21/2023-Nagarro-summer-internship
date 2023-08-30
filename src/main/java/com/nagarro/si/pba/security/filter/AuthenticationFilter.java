package com.nagarro.si.pba.security.filter;

import com.nagarro.si.pba.security.JWTGenerator;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@WebFilter
public class AuthenticationFilter implements Filter {
    private final JWTGenerator jwtGenerator;

    @Autowired
    public AuthenticationFilter(JWTGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String token = httpServletRequest.getHeader("Authorization");
        String requestURI = httpServletRequest.getRequestURI();

        if (requestURI.equals("/api/register") || requestURI.equals("/api/verification")
                || requestURI.equals("/api/login") || requestURI.equals("/api/reset-password")
                || requestURI.startsWith("/api/swagger") || requestURI.startsWith("/api/v3/api-docs")
                || requestURI.startsWith("/api/v3/api-docs/swagger-config")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        if (token == null || !token.startsWith("Bearer ")) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwtToken = token.replace("Bearer ", "");
        httpServletRequest.setAttribute("jwtToken", jwtToken);

        if (jwtGenerator.isTokenExpired(jwtToken)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
