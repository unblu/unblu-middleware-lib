package com.unblu.middleware.common.bootstrap;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Build step to register additional beans for the Unblu middleware.
 */
public class UnbluMiddlewareBuildStep {

    private static final String FEATURE = "unblu-middleware";
    private static final DotName ENABLE_COMMON = DotName.createSimple("com.unblu.middleware.common.annotation.EnableUnbluMiddlewareLibCommon");
    private static final DotName ENABLE_OUTBOUND = DotName.createSimple("com.unblu.middleware.outboundrequests.annotation.EnableUnbluOutboundRequests");
    private static final DotName ENABLE_WEBHOOKS = DotName.createSimple("com.unblu.middleware.webhooks.annotation.EnableUnbluWebhooks");
    private static final DotName ENABLE_DIALOG_BOT = DotName.createSimple("com.unblu.middleware.bots.annotation.EnableUnbluDialogBot");
    private static final DotName ENABLE_CONVERSATION_OBSERVING_BOT = DotName.createSimple("com.unblu.middleware.bots.annotation.EnableUnbluConversationObservingBot");
    private static final DotName ENABLE_EXTERNAL_MESSENGER = DotName.createSimple("com.unblu.middleware.externalmessenger.annotation.EnableUnbluExternalMessenger");

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void indexDependencies(BuildProducer<IndexDependencyBuildItem> indexedDependencies) {
        indexedDependencies.produce(new IndexDependencyBuildItem("com.unblu.middleware", "unblu-middleware-lib-core"));
        indexedDependencies.produce(new IndexDependencyBuildItem("com.unblu.middleware", "unblu-middleware-lib-quarkus"));
    }

    @BuildStep
    void registerBeans(CombinedIndexBuildItem combinedIndex, BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        var index = combinedIndex.getIndex();
        var flags = featureFlags(index);

        List<String> includedPrefixes = new ArrayList<>();
        if (flags.enableCommon()) {
            includedPrefixes.add("com.unblu.middleware.common.");
            includedPrefixes.add("com.unblu.middleware.bootstrap.unbluapi.");
            includedPrefixes.add("com.unblu.middleware.config.");
            includedPrefixes.add("com.unblu.middleware.quarkus.automation.");
            includedPrefixes.add("com.unblu.middleware.quarkus.error.");
        }
        if (flags.enableOutbound()) {
            includedPrefixes.add("com.unblu.middleware.outboundrequests.");
        }
        if (flags.enableWebhooks()) {
            includedPrefixes.add("com.unblu.middleware.webhooks.");
        }
        if (flags.enableDialogBot()) {
            includedPrefixes.add("com.unblu.middleware.bots.");
        }
        if (flags.enableExternalMessenger()) {
            includedPrefixes.add("com.unblu.middleware.externalmessenger.");
        }

        Set<String> beanClasses = new HashSet<>();
        var singleton = DotName.createSimple("jakarta.inject.Singleton");
        var appScoped = DotName.createSimple("jakarta.enterprise.context.ApplicationScoped");
        var requestScoped = DotName.createSimple("jakarta.enterprise.context.RequestScoped");
        var dependent = DotName.createSimple("jakarta.enterprise.context.Dependent");
        var produces = DotName.createSimple("jakarta.enterprise.inject.Produces");

        for (ClassInfo classInfo : index.getKnownClasses()) {
            var className = classInfo.name().toString();
            if (!startsWithAny(className, includedPrefixes)) {
                continue;
            }
            if (Modifier.isInterface(classInfo.flags()) || Modifier.isAbstract(classInfo.flags())) {
                continue;
            }

            var hasBeanDefiningScope = classInfo.hasDeclaredAnnotation(singleton)
                    || classInfo.hasDeclaredAnnotation(appScoped)
                    || classInfo.hasDeclaredAnnotation(requestScoped)
                    || classInfo.hasDeclaredAnnotation(dependent);
            var hasProducerMethod = classInfo.methods().stream().anyMatch(m -> m.hasDeclaredAnnotation(produces));

            if (hasBeanDefiningScope || hasProducerMethod) {
                beanClasses.add(className);
            }
        }

        if (flags.enableConversationObservingBot()) {
            beanClasses.add("com.unblu.middleware.bots.service.BotPersonRegistrationService");
        }

        // In extension mode, keep a small explicit seed list for the bot/outbound chain.
        if (flags.enableDialogBot()) {
            beanClasses.add("com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler");
            beanClasses.add("com.unblu.middleware.outboundrequests.handler.OutboundHandlerImpl");
            beanClasses.add("com.unblu.middleware.bots.service.DialogBotImpl");
            beanClasses.add("com.unblu.middleware.bots.service.DialogBotServiceImpl");
        }

        if (!beanClasses.isEmpty()) {
            additionalBeans.produce(AdditionalBeanBuildItem.builder()
                    .addBeanClasses(beanClasses.toArray(String[]::new))
                    .setUnremovable()
                    .build());
        }

        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass("com.unblu.middleware.quarkus.error.QuarkusFatalStartupErrorHandler")
                .setUnremovable()
                .build());
    }

    @BuildStep
    void excludeDisabledFeaturePackages(
            CombinedIndexBuildItem combinedIndex,
            BuildProducer<ExcludedTypeBuildItem> excludedTypes) {
        var flags = featureFlags(combinedIndex.getIndex());

        if (!flags.enableCommon()) {
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.common.*"));
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.bootstrap.unbluapi.*"));
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.config.*"));
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.quarkus.automation.*"));
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.quarkus.error.*"));
        }
        if (!flags.enableOutbound()) {
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.outboundrequests.*"));
        }
        if (!flags.enableWebhooks()) {
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.webhooks.*"));
        }
        if (!flags.enableDialogBot() && !flags.enableConversationObservingBot()) {
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.bots.*"));
        }
        if (!flags.enableExternalMessenger()) {
            excludedTypes.produce(new ExcludedTypeBuildItem("com.unblu.middleware.externalmessenger.*"));
        }
    }

    private static FeatureFlags featureFlags(org.jboss.jandex.IndexView index) {
        boolean hasCommonAnnotation = hasAnnotation(index.getAnnotations(ENABLE_COMMON));
        boolean hasOutboundAnnotation = hasAnnotation(index.getAnnotations(ENABLE_OUTBOUND));
        boolean hasWebhooksAnnotation = hasAnnotation(index.getAnnotations(ENABLE_WEBHOOKS));
        boolean hasDialogAnnotation = hasAnnotation(index.getAnnotations(ENABLE_DIALOG_BOT));
        boolean hasConversationAnnotation = hasAnnotation(index.getAnnotations(ENABLE_CONVERSATION_OBSERVING_BOT));
        boolean hasExternalMessengerAnnotation = hasAnnotation(index.getAnnotations(ENABLE_EXTERNAL_MESSENGER));

        boolean enableCommon = hasCommonAnnotation
                || hasOutboundAnnotation
                || hasWebhooksAnnotation
                || hasDialogAnnotation
                || hasConversationAnnotation
                || hasExternalMessengerAnnotation;
        boolean enableOutbound = hasOutboundAnnotation
                || hasDialogAnnotation
                || hasExternalMessengerAnnotation;
        boolean enableWebhooks = hasWebhooksAnnotation
                || hasConversationAnnotation;
        boolean enableDialogBot = hasDialogAnnotation;
        boolean enableConversationObservingBot = hasConversationAnnotation;
        boolean enableExternalMessenger = hasExternalMessengerAnnotation;
        return new FeatureFlags(
                enableCommon,
                enableOutbound,
                enableWebhooks,
                enableDialogBot,
                enableConversationObservingBot,
                enableExternalMessenger
        );
    }

    private static boolean hasAnnotation(Collection<AnnotationInstance> annotations) {
        return annotations != null && !annotations.isEmpty();
    }

    private static boolean startsWithAny(String value, List<String> prefixes) {
        for (String prefix : prefixes) {
            if (value.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private record FeatureFlags(
            boolean enableCommon,
            boolean enableOutbound,
            boolean enableWebhooks,
            boolean enableDialogBot,
            boolean enableConversationObservingBot,
            boolean enableExternalMessenger) {
    }
}
