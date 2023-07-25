package com.brew.oauth20.server.middleware;

import com.brew.oauth20.server.fixture.WebOriginModelFixture;
import com.brew.oauth20.server.model.WebOriginModel;
import com.brew.oauth20.server.service.ClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.DelegatingServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CORSMiddlewareTest {

    private final List<WebOriginModel> webOriginModels;
    @Mock
    private ClientService clientService;
    @InjectMocks
    private CORSMiddleware corsMiddleware;

    public CORSMiddlewareTest() {
        var webOriginModelFixture = new WebOriginModelFixture();
        webOriginModels = webOriginModelFixture.createRandomList(2);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilterInternal_WithValidClientId_AddsCorsConfiguration() throws ServletException, IOException {
        // Arrange
        // Mocking the request and response objects
        var clientId = "testClient";
        var expectedOrigin = webOriginModels.stream()
                .map(WebOriginModel::webOrigin)
                .findFirst()
                .get();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Setting up request headers
        when(request.getHeader("Origin")).thenReturn(expectedOrigin);
        when(request.getParameter("client_id")).thenReturn(clientId);
        when(request.getMethod()).thenReturn("POST");

        // Create a list of header names containing just "Origin"
        List<String> headerNamesList = new ArrayList<>();
        headerNamesList.add("Origin");

        // Create an Enumeration for header names
        Enumeration<String> headerNamesEnum = Collections.enumeration(headerNamesList);

        // Create a mock for the Enumeration of header values for "Origin" header
        Enumeration<String> headerValuesEnum = Mockito.mock(Enumeration.class);
        when(headerValuesEnum.hasMoreElements()).thenReturn(true).thenReturn(false); // Simulate having at least one value
        when(headerValuesEnum.nextElement()).thenReturn(expectedOrigin);

        // Mock the getHeaderNames() method to return the Enumeration
        when(request.getHeaderNames()).thenReturn(headerNamesEnum);

        // Mock the getHeaders(String name) method to return the Enumeration for "Origin" header
        when(request.getHeaders("Origin")).thenReturn(headerValuesEnum);

        // Mocking the clientService behavior
        when(clientService.getWebOrigins(clientId)).thenReturn(webOriginModels);

        // Act
        // Perform the filter operation
        corsMiddleware.doFilterInternal(request, response, filterChain);

        // Assert
        // Verify if the CorsConfiguration was set on the response
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testReadClientIdFromBody_ValidRequestBody_ReturnsClientId() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        var clientId = "testClientId";
        var requestBody = "{\"client_id\": \"" + clientId + "\"}";
        byte[] inputStreamBytes = requestBody.getBytes();

        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(new ByteArrayInputStream(inputStreamBytes));
        when(request.getInputStream()).thenReturn(servletInputStream);

        // Access the private method readClientIdFromBody using reflection
        Method readClientIdFromBodyMethod = CORSMiddleware.class.getDeclaredMethod("readClientIdFromBody", HttpServletRequest.class);
        readClientIdFromBodyMethod.setAccessible(true);

        // Act
        String result = (String) readClientIdFromBodyMethod.invoke(corsMiddleware, request);

        // Assert
        assertEquals(clientId, result, "The extracted clientId should match the expected value");
    }


    @Test
    void testDoFilterInternal_NoOriginHeader_DoesNotAddCorsConfiguration() throws ServletException, IOException {
        // Mocking the request and response objects
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Setting up request headers
        when(request.getHeader("Origin")).thenReturn(null);

        // Perform the filter operation
        corsMiddleware.doFilterInternal(request, response, filterChain);

        // Verify that no CorsConfiguration was added to the response
        verify(response, never()).setHeader(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidClientId_ThrowsIllegalStateException() throws ServletException, IOException {
        // Mocking the request and response objects
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Setting up request headers
        when(request.getHeader("Origin")).thenReturn("https://example.com");
        when(request.getHeader("Authorization")).thenReturn("Bearer TOKEN");

        // Mocking the clientService behavior
        String clientId = "nonExistentClient";
        when(clientService.getWebOrigins(clientId)).thenReturn(Collections.emptyList());

        // Perform the filter operation and check for the IllegalStateException
        assertThrows(IllegalStateException.class, () -> corsMiddleware.doFilterInternal(request, response, filterChain));
    }

}
