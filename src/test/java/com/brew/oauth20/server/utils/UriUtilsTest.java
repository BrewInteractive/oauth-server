package com.brew.oauth20.server.utils;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UriUtilsTest {
    private static Stream<Arguments> should_validate_uri_list() {
        return Stream.of(
                Arguments.of("https://www.google.com", true),
                Arguments.of("https://www.test.istanbul", true),
                Arguments.of("https://www.test-test.istanbul", true),
                Arguments.of("http://github.com", true),
                Arguments.of("https://example.co.uk/test/page.html", true),
                Arguments.of("www.example.com", true),
                Arguments.of("www.example-test.com", true),
                Arguments.of("example.com", true),
                Arguments.of("example.com/test.html?param=value", true),
                Arguments.of("example.info/test-page.html", true),
                Arguments.of("google.com", true),
                Arguments.of("example.info?param=value#fragment", true),
                Arguments.of("http://example.com", true),
                Arguments.of("http://exampl!'^e.com", false),
                Arguments.of("google", false),
                Arguments.of("http://.example.com", false),
                Arguments.of("ftp://example.com", false),
                Arguments.of("ftp://example.", false),
                Arguments.of("ftp://example", false),
                Arguments.of("www.example-.com", false),
                Arguments.of("example.com/test file.html", false),
                Arguments.of("example.com?query=invalid&param=value%Z", false)
        );
    }

    @MethodSource
    @ParameterizedTest
    void should_validate_uri_list(String uri, boolean expected) {
        assertEquals(expected, UriUtils.isValidUrl(uri));
    }
}
