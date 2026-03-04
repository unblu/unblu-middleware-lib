package com.unblu.middleware.common.bootstrap;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

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
    void indexDependencies(BuildProducer<IndexDependencyBuildItem> indexedDependencies) {
        indexedDependencies.produce(new IndexDependencyBuildItem("com.unblu.middleware", "unblu-middleware-lib-core"));
        indexedDependencies.produce(new IndexDependencyBuildItem("com.unblu.middleware", "unblu-middleware-lib-quarkus"));
    }

    @BuildStep
    void registerBeans(CombinedIndexBuildItem combinedIndex, BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        Set<String> beanClasses = new HashSet<>();
        var index = combinedIndex.getIndex();
        var singleton = DotName.createSimple("jakarta.inject.Singleton");
        var appScoped = DotName.createSimple("jakarta.enterprise.context.ApplicationScoped");
        var requestScoped = DotName.createSimple("jakarta.enterprise.context.RequestScoped");
        var dependent = DotName.createSimple("jakarta.enterprise.context.Dependent");
        var produces = DotName.createSimple("jakarta.enterprise.inject.Produces");

        for (ClassInfo classInfo : index.getKnownClasses()) {
            var className = classInfo.name().toString();
            if (!className.startsWith("com.unblu.middleware.")) {
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

        // In extension mode, keep a minimal explicit seed list for critical core beans.
        beanClasses.add("com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler");
        beanClasses.add("com.unblu.middleware.outboundrequests.handler.OutboundHandlerImpl");
        beanClasses.add("com.unblu.middleware.bots.service.DialogBotImpl");
        beanClasses.add("com.unblu.middleware.bots.service.DialogBotServiceImpl");

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
}
