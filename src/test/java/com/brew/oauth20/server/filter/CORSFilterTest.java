package com.brew.oauth20.server.filter;

import com.brew.oauth20.server.exception.ClientAuthenticationFailedException;
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
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CORSFilterTest {

    private final List<WebOriginModel> webOriginModels;
    @Mock
    private ClientService clientService;
    @InjectMocks
    private CORSFilter corsFilter;

    public CORSFilterTest() {
        var webOriginModelFixture = new WebOriginModelFixture();
        webOriginModels = webOriginModelFixture.createRandomList(2);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_add_cors_configuration_with_valid_client_id() throws ServletException, IOException {
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
        corsFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Verify if the CorsConfiguration was set on the response
        verify(filterChain).doFilter(any(), eq(response));
    }

    @Test
    void should_read_client_id_from_valid_request_body() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        var clientId = "testClientId";
        var requestBody = "{\"client_id\": \"" + clientId + "\"}";
        byte[] inputStreamBytes = requestBody.getBytes();

        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(new ByteArrayInputStream(inputStreamBytes));
        when(request.getInputStream()).thenReturn(servletInputStream);

        // Access the private method readClientIdFromBody using reflection
        Method readClientIdFromBodyMethod = CORSFilter.class.getDeclaredMethod("readClientIdFromBody", HttpServletRequest.class);
        readClientIdFromBodyMethod.setAccessible(true);

        // Act
        String result = (String) readClientIdFromBodyMethod.invoke(corsFilter, request);

        // Assert
        assertEquals(clientId, result, "The extracted clientId should match the expected value");
    }

    @Test
    void should_set_response_status_to_200_for_options_request() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        var origin = "https://example.com";

        // Use a real HttpServletResponse instance
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Setting up the request to have "OPTIONS" method
        when(request.getHeader("Origin")).thenReturn(origin);
        when(request.getMethod()).thenReturn("OPTIONS");

        // Act
        corsFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Verify that the response status is set to SC_OK (200)
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        // Verify that the headers are set correctly for an OPTIONS request
        assertEquals(origin, response.getHeader("Access-Control-Allow-Origin"));
        assertEquals("GET, POST, OPTIONS, HEAD", response.getHeader("Access-Control-Allow-Methods"));
        assertEquals("Authorization, Content-Type", response.getHeader("Access-Control-Allow-Headers"));
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"));
        // Verify that no other method is called on the response object
        assertNull(response.getContentType());

        // Verify that the filterChain is called
        verify(filterChain).doFilter(any(), eq(response));
    }

    @Test
    void should_extract_origin_from_referer_header() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Set the "Referer" header in the request
        String refererHeader = "https://example.com:8080/somepath";
        when(request.getHeader("Referer")).thenReturn(refererHeader);
        when(request.getMethod()).thenReturn("OPTIONS");

        // Act
        corsFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Verify that the response has the expected "Access-Control-Allow-Origin" header
        verify(response).setHeader("Access-Control-Allow-Origin", "https://example.com:8080");
        // Verify that other headers are set correctly
        verify(response).addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, HEAD");
        verify(response).addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        verify(response).addHeader("Access-Control-Allow-Credentials", "true");
        verify(filterChain).doFilter(any(), eq(response));
    }


    @Test
    void should_not_add_cors_configuration_when_no_origin_header_is_present() throws ServletException, IOException {
        // Mocking the request and response objects
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Setting up request headers
        when(request.getHeader("Origin")).thenReturn(null);
        // Setting up the request to have "POST" method
        when(request.getMethod()).thenReturn("POST");

        // Perform the filter operation
        corsFilter.doFilterInternal(request, response, filterChain);

        // Verify that no CorsConfiguration was added to the response
        verify(response, never()).setHeader(anyString(), anyString());
        verify(filterChain).doFilter(any(), eq(response));
    }

    @Test
    void should_throw_client_authentication_failed_for_invalid_client_id() {
        // Mocking the request and response objects
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Setting up request headers
        when(request.getHeader("Origin")).thenReturn("https://example.com");
        when(request.getHeader("Authorization")).thenReturn("Bearer TOKEN");
        when(request.getMethod()).thenReturn("POST");

        // Mocking the clientService behavior
        String clientId = "nonExistentClient";
        when(clientService.getWebOrigins(clientId)).thenReturn(Collections.emptyList());

        // Perform the filter operation and check for the IllegalStateException
        assertThrows(ClientAuthenticationFailedException.class, () -> corsFilter.doFilterInternal(request, response, filterChain));
    }

    @Test
    void should_return_true_when_is_finished() {
        // Arrange
        InputStream inputStream = new ByteArrayInputStream(new byte[0]); // Create an empty input stream
        CORSFilter.CachedBodyServletInputStream cachedBodyInputStream = new CORSFilter.CachedBodyServletInputStream(inputStream);

        // Act
        boolean result = cachedBodyInputStream.isFinished();

        // Assert
        assertTrue(result, "The isFinished method should return true for an empty input stream.");
    }

    @Test
    void should_isFinisted_return_false_when_is_not_finished() {
        // Arrange
        InputStream inputStream = new ByteArrayInputStream(new byte[1]); // Create an input stream with one byte
        CORSFilter.CachedBodyServletInputStream cachedBodyInputStream = new CORSFilter.CachedBodyServletInputStream(inputStream);

        // Act
        boolean result = cachedBodyInputStream.isFinished();

        // Assert
        assertFalse(result, "The isFinished method should return false for a non-empty input stream.");
    }

    @Test
    void should_isFinisted_return_false_when_IOException_is_thrown() throws IOException {
        // Arrange
        InputStream inputStream = Mockito.mock(InputStream.class);
        // Configure the mock to throw an IOException when available() is called
        Mockito.when(inputStream.available()).thenThrow(new IOException());

        CORSFilter.CachedBodyServletInputStream cachedBodyInputStream = new CORSFilter.CachedBodyServletInputStream(inputStream);

        // Act
        boolean result = cachedBodyInputStream.isFinished();

        // Assert
        assertFalse(result, "The isFinished method should return false and catch an IOException.");
    }

    @Test
    void should_isReady_always_return_true() {
        // Arrange
        CORSFilter.CachedBodyServletInputStream cachedBodyInputStream = new CORSFilter.CachedBodyServletInputStream(new ByteArrayInputStream(new byte[0]));

        // Act
        boolean result = cachedBodyInputStream.isReady();

        // Assert
        assertTrue(result, "The isReady method should always return true.");
    }

    @Test
    void setReadListener_should_not_throw_exceptions() {
        // Arrange
        CORSFilter.CachedBodyServletInputStream cachedBodyInputStream = new CORSFilter.CachedBodyServletInputStream(new ByteArrayInputStream(new byte[0]));

        // Act and Assert
        assertDoesNotThrow(() -> cachedBodyInputStream.setReadListener(null), "The setReadListener method should not throw exceptions.");
    }
}
