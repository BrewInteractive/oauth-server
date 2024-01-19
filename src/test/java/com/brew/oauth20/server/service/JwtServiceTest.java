package com.brew.oauth20.server.service;

import com.brew.oauth20.server.model.SignTokenOptions;
import com.brew.oauth20.server.service.impl.JwtServiceImpl;
import com.github.javafaker.Faker;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtServiceTest {
    private static Faker faker;

    @BeforeAll
    public static void init() {
        faker = new Faker();
    }

    private static Stream<Arguments> should_sign_token_without_subject() {
        return Stream.of(
                Arguments.of(
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.letterify("?".repeat(32)),
                        faker.random().nextInt(1, Integer.MAX_VALUE),
                        new HashMap<String, Object>() {{
                            put("additional_value", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                        }}
                ),
                Arguments.of(
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.letterify("?".repeat(32)),
                        faker.random().nextInt(1, Integer.MAX_VALUE),
                        null
                )
        );
    }

    private static Stream<Arguments> should_sign_token_with_subject() {
        return Stream.of(
                Arguments.of(
                        String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)),
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.letterify("?".repeat(32)),
                        faker.random().nextInt(1, Integer.MAX_VALUE),
                        new HashMap<String, Object>() {{
                            put("client_id", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                            put("user_id", String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)));
                        }}
                ),
                Arguments.of(
                        String.valueOf(faker.random().nextInt(Integer.MAX_VALUE)),
                        faker.internet().url(),
                        faker.internet().url(),
                        faker.letterify("?".repeat(32)),
                        faker.random().nextInt(1, Integer.MAX_VALUE),
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void should_sign_token_without_subject(String audience,
                                           String issuerUri,
                                           String signingKey,
                                           Integer expiresInSeconds,
                                           HashMap<String, Object> additionalClaims) {

        // Act
        var jwtService = new JwtServiceImpl();
        var result = jwtService.signToken(new SignTokenOptions(null, audience, issuerUri, signingKey, expiresInSeconds, additionalClaims));

        // Assert
        var claims = parseClaims(result, signingKey);
        assertThat(result).isNotBlank();
        assertThat(result.length()).isBetween(100, 1000);
        assertThat(claims.getAudience()).isEqualTo(audience);
        assertThat(claims.getIssuer()).isEqualTo(issuerUri);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        if (additionalClaims != null)
            for (var entry : additionalClaims.entrySet())
                assertThat(claims).containsEntry(entry.getKey(), entry.getValue());

    }

    @ParameterizedTest
    @MethodSource
    void should_sign_token_with_subject(String subject,
                                        String audience,
                                        String issuerUri,
                                        String signingKey,
                                        Integer expiresInSeconds,
                                        HashMap<String, Object> additionalClaims) {

        // Act
        var jwtService = new JwtServiceImpl();
        var result = jwtService.signToken(new SignTokenOptions(subject, audience, issuerUri, signingKey, expiresInSeconds, additionalClaims));

        // Assert
        var claims = parseClaims(result, signingKey);
        assertThat(result).isNotBlank();
        assertThat(result.length()).isBetween(100, 1000);
        assertThat(claims.getSubject()).isEqualTo(subject);
        assertThat(claims.getAudience()).isEqualTo(audience);
        assertThat(claims.getIssuer()).isEqualTo(issuerUri);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        if (additionalClaims != null)
            for (var entry : additionalClaims.entrySet())
                assertThat(claims).containsEntry(entry.getKey(), entry.getValue());
    }


    private Claims parseClaims(String token, String signingKey) {
        var parser = Jwts.parserBuilder().setSigningKey(getSigningKey(signingKey)).build();
        return parser.parseClaimsJws(token).getBody();
    }

    private Key getSigningKey(String signingKey) {
        byte[] keyBytes = signingKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}