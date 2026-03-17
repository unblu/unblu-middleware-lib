package com.unblu.middleware.quarkus.externalmessenger.config;

import com.unblu.middleware.config.DtoBinder;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped
public class ExternalMessengerConfigurationProducer {

    private final SmallRyeConfig config;
    private final DtoBinder binder;

    public ExternalMessengerConfigurationProducer(SmallRyeConfig config, DtoBinder binder) {
        this.config = config;
        this.binder = binder;
    }

    @Produces
    @Singleton
    public ExternalMessengerConfiguration externalMessengerConfiguration() {
        return binder.bindPrefix(config, "unblu.external-messenger", ExternalMessengerConfiguration.class);
    }
}
