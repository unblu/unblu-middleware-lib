package com.unblu.middleware.integration;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for configuration binding.
 * Tests that Quarkus configuration is properly bound to configuration objects.
 */
@QuarkusTest
class ConfigurationIntegrationTest {

    @Inject
    MiddlewareConfiguration middlewareConfiguration;

    @Test
    void testMiddlewareConfigurationIsInjected() {
        assertNotNull(middlewareConfiguration, "MiddlewareConfiguration should be injected");
    }

    @Test
    void testMiddlewareConfigurationIsPopulated() {
        assertNotNull(middlewareConfiguration.url(), "Middleware URL should be configured");
        assertEquals("http://localhost:9090", middlewareConfiguration.url(),
            "Middleware URL should match test configuration");

        assertNotNull(middlewareConfiguration.name(), "Middleware name should be configured");
        assertEquals("test-middleware", middlewareConfiguration.name(),
            "Middleware name should match test configuration");
    }

    @Test
    void testAutoConfigurationFlags() {
        assertFalse(middlewareConfiguration.autoSubscribe(),
            "Auto-subscribe should be disabled in test configuration");
        assertFalse(middlewareConfiguration.autoRegister(),
            "Auto-register should be disabled in test configuration");
        assertFalse(middlewareConfiguration.selfHealingEnabled(),
            "Self-healing should be disabled in test configuration");
        assertFalse(middlewareConfiguration.pingUnbluOnStartup(),
            "Ping on startup should be disabled in test configuration");
    }
}
