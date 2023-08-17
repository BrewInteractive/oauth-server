package com.brew.oauth20.server.filter;

import com.brew.oauth20.server.model.WebOriginModel;
import com.brew.oauth20.server.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter extends OncePerRequestFilter {
    @Autowired
    ClientService clientService;

    private static String readClientIdFromBody(HttpServletRequest request) {
        String clientId;
        try {
            byte[] inputStreamBytes = StreamUtils.copyToByteArray(request.getInputStream());
            Map<String, String> jsonRequest = new ObjectMapper().readValue(inputStreamBytes, Map.class);
            clientId = jsonRequest.get("client_id");

        } catch (IOException e) {
            return null;
        }
        return clientId;
    }

    private static String readClientIfFromQueryString(HttpServletRequest request) {
        return request.getParameter("client_id");
    }

    private static void addCorsConfiguration(HttpServletRequest request, HttpServletResponse response, List<String> webOrigins) throws IOException {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(webOrigins);

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "HEAD"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization"));


        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        if (request.getMethod().equals("OPTIONS")) {
            // For OPTIONS requests, do not write a response body
            response.setStatus(HttpServletResponse.SC_OK);
        }
        // Use the built-in CorsProcessor provided by Spring to handle CORS and apply headers to the response
        var corsProcessor = new DefaultCorsProcessor();

        corsProcessor.processRequest(configuration, request, response);


    }


    @Nullable
    private String readClientId(HttpServletRequest request) {
        var clientId = readClientIdFromAuthorizationHeader(request);
        if (clientId == null)
            clientId = readClientIfFromQueryString(request);
        if (clientId == null)
            clientId = readClientIdFromBody(request);
        return clientId;
    }

    private String readClientIdFromAuthorizationHeader(HttpServletRequest request) {
        String clientId = null;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            var clientCredentials = clientService.decodeClientCredentials(authorizationHeader);
            if (clientCredentials.isPresent())
                clientId = clientCredentials.get().getFirst();
        }
        return clientId;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader("Referer") != null) {
            // This custom middleware is going to first pull the client_id from the request
            // and verify that the client is allowing cors origins
            var clientId = readClientId(request);
            if (clientId == null || clientId.isBlank())
                throw new IllegalStateException("Can't find CORS Configuration");
            else {
                var webOrigins = clientService.getWebOrigins(clientId);

                addCorsConfiguration(request, response, webOrigins.stream()
                        .map(WebOriginModel::webOrigin)
                        .toList());
            }
        }
        filterChain.doFilter(request, response);
    }
}
