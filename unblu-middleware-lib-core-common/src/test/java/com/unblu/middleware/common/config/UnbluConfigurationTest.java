package com.unblu.middleware.common.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnbluConfigurationTest {

    @Test
    void basicAuthOnlyIsValid() {
        UnbluConfiguration cfg = new UnbluConfiguration("https://u", "/app/rest/v4", "user", "pw", null, null, null);
        assertEquals("user", cfg.user());
        assertEquals("pw", cfg.password());
    }

    @Test
    void bearerTokenOnlyIsValid() {
        UnbluConfiguration cfg = new UnbluConfiguration("https://u", "/app/rest/v4", null, null, "tkn", null, null);
        assertEquals("tkn", cfg.bearerToken());
    }

    @Test
    void blankBasicAuthWithBearerTokenIsValid() {
        UnbluConfiguration cfg = new UnbluConfiguration("https://u", "/app/rest/v4", "", "", "tkn", null, null);
        assertEquals("tkn", cfg.bearerToken());
    }

    @Test
    void noAuthIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new UnbluConfiguration("https://u", "/app/rest/v4", null, null, null, null, null));
    }

    @Test
    void bothAuthMethodsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new UnbluConfiguration("https://u", "/app/rest/v4", "user", "pw", "tkn", null, null));
    }

    @Test
    void partialBasicAuthIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new UnbluConfiguration("https://u", "/app/rest/v4", "user", null, null, null, null));
    }
}
