package com.unblu.middleware.common.bootstrap;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.DotName;

/**
 * Build step to register additional beans for the Unblu middleware.
 */
public class UnbluMiddlewareBuildStep {

    private static final String FEATURE = "unblu-middleware";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        // Register all beans from runtime module using package scanning
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClasses(
                        "com.unblu.middleware.bootstrap.unbluapi.UnbluApiFactory",
                        "com.unblu.middleware.bootstrap.unbluapi.UnbluApiClientBootstrap",
                        "com.unblu.middleware.config.ConfigurationProducer",
                        "com.unblu.middleware.config.DtoBinder",
                        "com.unblu.middleware.quarkus.automation.AutomationBootstrap",
                        "com.unblu.middleware.outboundrequests.OutboundRoute",
                        "com.unblu.middleware.webhooks.WebhookRoute",
                        "com.unblu.middleware.quarkus.error.QuarkusFatalStartupErrorHandler",
                        "com.unblu.middleware.webhooks.controller.WebhookControllerService",
                        "com.unblu.middleware.outboundrequests.controller.OutboundRequestsControllerService",
                        "com.unblu.middleware.outboundrequests.handler.OutboundHandlerImpl",
                        "com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler",
                        "com.unblu.middleware.bots.service.DialogBotImpl",
                        "com.unblu.middleware.bots.service.DialogBotServiceImpl"
                )
                .setUnremovable()
                .setDefaultScope(DotName.createSimple("jakarta.enterprise.context.ApplicationScoped"))
                .build());
    }
}
