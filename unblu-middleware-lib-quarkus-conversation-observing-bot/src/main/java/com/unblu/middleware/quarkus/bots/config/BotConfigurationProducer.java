package com.unblu.middleware.quarkus.bots.config;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.config.DtoBinder;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped
public class BotConfigurationProducer {

    private final SmallRyeConfig config;
    private final DtoBinder binder;

    public BotConfigurationProducer(SmallRyeConfig config, DtoBinder binder) {
        this.config = config;
        this.binder = binder;
    }

    @Produces
    @Singleton
    public BotConfiguration botConfiguration() {
        return binder.bindPrefix(config, "unblu.bot", BotConfiguration.class);
    }
}
