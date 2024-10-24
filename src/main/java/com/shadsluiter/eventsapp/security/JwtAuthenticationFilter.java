package com.shadsluiter.eventsapp.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// This class is responsible for authenticating the user based on the JWT token.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // This method is called for every request that comes to the server.
    // It extracts the JWT token from the request, validates it, and sets the user's authentication in the SecurityContext.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // extract the JWT token from the request
        String jwt = getJwtFromRequest(request);

        // validate the token
        if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
            String username = jwtTokenProvider.getUsernameFromJWT(jwt);

            // load the user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                // create an authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // set the authentication object in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // continue the filter chain
        filterChain.doFilter(request, response);
    }

    // extract the JWT token from the request
    private String getJwtFromRequest(HttpServletRequest request) {
        // the token is sent in the Authorization header
        String bearerToken = request.getHeader("Authorization");
        // the token starts with "Bearer " so we need to remove it
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
