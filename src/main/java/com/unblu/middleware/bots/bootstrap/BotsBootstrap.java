package com.unblu.middleware.bots.bootstrap;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;

@Configuration
@RequiredArgsConstructor
public class BotsBootstrap {

    @Bean
    @Qualifier("botsRequestHandler")
    public RequestHandler botsRequestHandler(DataBufferFactory dataBufferFactory, BotConfiguration botConfiguration) {
        return new RequestHandler(dataBufferFactory, new RequestHandlerConfiguration(botConfiguration.getSecret()));
    }
}