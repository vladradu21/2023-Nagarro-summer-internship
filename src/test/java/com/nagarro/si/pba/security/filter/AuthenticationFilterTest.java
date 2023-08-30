package com.nagarro.si.pba.security.filter;

import com.nagarro.si.pba.security.JWTGenerator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {
    @Mock
    private JWTGenerator jwtGenerator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @Test
    void testDoFilter_WithValidToken_ShouldCallFilterChain() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/user");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtGenerator.isTokenExpired("validToken")).thenReturn(false);

        authenticationFilter.doFilter(request, response, filterChain);

        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_WithUnsecuredEndpoint_ShouldNotSetStatusAndCallFilterChain() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/register");

        authenticationFilter.doFilter(request, response, filterChain);

        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_WithMissingToken_ShouldSetStatusUnauthorized() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/user");
        when(request.getHeader("Authorization")).thenReturn(null);

        authenticationFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilter_WithExpiredToken_ShouldSetStatusUnauthorized() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/user");
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(jwtGenerator.isTokenExpired("expiredToken")).thenReturn(true);

        authenticationFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @ParameterizedTest
    @MethodSource("urlProvider")
    public void testDoFilter_SwaggerPath(String url) throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn(url);

        authenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private static Stream<Arguments> urlProvider() {
        return Arrays.stream(new Arguments[] {
                Arguments.of("/api/swagger-ui/index.html"),
                Arguments.of("/api/v3/api-docs"),
                Arguments.of("/api/v3/api-docs/swagger-config")
        });
    }
}
