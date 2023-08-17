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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.MalformedURLException;
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

        if (request.getMethod().equals("OPTIONS")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "false");
            // For OPTIONS requests, do not write a response body
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            var origin = getOrigin(request);


            var allowedOrigins = webOrigins.stream().map(CORSFilter::trimTrailingSlash).toList();

            if (origin != null && allowedOrigins.contains(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, HEAD");
                response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
                response.addHeader("Access-Control-Allow-Credentials", "true");
            }
        }

    }

    private static String getOrigin(HttpServletRequest request) throws MalformedURLException {
        var origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank() || origin.isEmpty()) {
            // Get the Referer header from the request
            String refererHeader = request.getHeader("Referer");

            if (refererHeader != null) {
                // Parse the Referer URL
                java.net.URL refererURL = new java.net.URL(refererHeader);

                // Extract the origin (scheme + host)
                origin = refererURL.getProtocol() + "://" + refererURL.getHost();
                if (refererURL.getPort() != -1)
                    origin += ":" + refererURL.getPort();
            }
        }

        return (origin != null ? trimTrailingSlash(origin) : null);
    }

    private static String trimTrailingSlash(String url) {
        return (url.endsWith("/") ? url.substring(0, url.length() - 1) : url);
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
        if (request.getHeader("Origin") != null || request.getHeader("Referer") != null) {
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
