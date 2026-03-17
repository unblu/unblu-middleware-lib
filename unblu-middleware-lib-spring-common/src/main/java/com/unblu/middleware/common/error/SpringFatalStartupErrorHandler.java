package com.unblu.middleware.common.error;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringFatalStartupErrorHandler implements FatalStartupErrorHandler {

    private final ApplicationContext context;

    public void shutdown() {
        if (context instanceof ConfigurableApplicationContext ctx) {
            ctx.close();
        }
    }
}
