package com.unblu.middleware.integration;

import com.unblu.middleware.bots.annotation.EnableUnbluDialogBot;
import com.unblu.middleware.bots.service.DialogBot;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@EnableUnbluDialogBot
class DialogBotIntegrationTest {

    @Inject
    DialogBot dialogBot;

    @Test
    void dialogBotBean_shouldBeInjected() {
        assertNotNull(dialogBot, "DialogBot should be injected");
    }
}
