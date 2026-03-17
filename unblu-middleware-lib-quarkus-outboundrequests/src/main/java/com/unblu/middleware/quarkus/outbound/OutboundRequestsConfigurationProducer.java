package com.unblu.middleware.quarkus.outbound;

import com.unblu.middleware.config.DtoBinder;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped
public class OutboundRequestsConfigurationProducer {

    private final SmallRyeConfig config;
    private final DtoBinder binder;

    public OutboundRequestsConfigurationProducer(SmallRyeConfig config, DtoBinder binder) {
        this.config = config;
        this.binder = binder;
    }

    @Produces
    @Singleton
    public OutboundRequestsConfiguration outboundRequestsConfiguration() {
        return binder.bindPrefix(config, "unblu.outbound-requests", OutboundRequestsConfiguration.class);
    }
}
