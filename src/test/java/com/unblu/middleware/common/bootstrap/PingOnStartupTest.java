package com.unblu.middleware.common.bootstrap;

import com.unblu.webapi.jersey.v4.api.GlobalApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "unblu.middleware.ping-unblu-on-startup=true",
})
class PingOnStartupTest {

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    GlobalApi globalApi;

    @Test
    void givenPingUnbluOnStartupConfig_onAppStartup_pingIsSent() throws ApiException {
        verify(globalApi, atLeastOnce()).globalPing();
    }
}
