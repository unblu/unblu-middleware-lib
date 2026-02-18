package com.unblu.middleware.common.bootstrap;

import com.unblu.middleware.bootstrap.unbluapi.dynamic.*;
import io.quarkus.arc.BeanCreator;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;

import java.util.Map;

/**
 * CDI producer for Unblu API client and all API instances.
 * All APIs are produced as @ApplicationScoped beans that can be injected.
 */
public class DynamicUnbluApis {

    private static final Map<String, Class<? extends BeanCreator<Object>>> CLASSES = Map.ofEntries(
            Map.entry("AccountsApi", AccountsApiBeanCreator.class),
            Map.entry("ApiKeysApi", ApiKeysApiBeanCreator.class),
            Map.entry("AuditApi", AuditApiBeanCreator.class),
            Map.entry("AuthenticatorApi", AuthenticatorApiBeanCreator.class),
            Map.entry("AvailabilityApi", AvailabilityApiBeanCreator.class),
            Map.entry("AvatarsApi", AvatarsApiBeanCreator.class),
            Map.entry("BranchClientStatesApi", BranchClientStatesApiBeanCreator.class),
            Map.entry("BranchClientsApi", BranchClientsApiBeanCreator.class),
            Map.entry("BranchesApi", BranchesApiBeanCreator.class),
            Map.entry("CallsApi", CallsApiBeanCreator.class),
            Map.entry("CannedResponsesApi", CannedResponsesApiBeanCreator.class),
            Map.entry("CollaborationLayerEventsApi", CollaborationLayerEventsApiBeanCreator.class),
            Map.entry("CollaborationLayerLogsApi", CollaborationLayerLogsApiBeanCreator.class),
            Map.entry("ConversationHistoryApi", ConversationHistoryApiBeanCreator.class),
            Map.entry("ConversationTemplatesApi", ConversationTemplatesApiBeanCreator.class),
            Map.entry("DeputyRelationshipsApi", DeputyRelationshipsApiBeanCreator.class),
            Map.entry("DomainsApi", DomainsApiBeanCreator.class),
            Map.entry("ExternalMessengerContactsApi", ExternalMessengerContactsApiBeanCreator.class),
            Map.entry("FileUploadGlobalInterceptorsApi", FileUploadGlobalInterceptorsApiBeanCreator.class),
            Map.entry("FileUploadInterceptorsApi", FileUploadInterceptorsApiBeanCreator.class),
            Map.entry("ImagesApi", ImagesApiBeanCreator.class),
            Map.entry("InvitationsApi", InvitationsApiBeanCreator.class),
            Map.entry("MessageInterceptorsApi", MessageInterceptorsApiBeanCreator.class),
            Map.entry("NamedAreasApi", NamedAreasApiBeanCreator.class),
            Map.entry("PersonLabelsApi", PersonLabelsApiBeanCreator.class),
            Map.entry("PersonPresencesApi", PersonPresencesApiBeanCreator.class),
            Map.entry("PersonVisibilityRulesApi", PersonVisibilityRulesApiBeanCreator.class),
            Map.entry("RecordRetentionApi", RecordRetentionApiBeanCreator.class),
            Map.entry("SearchIndexApi", SearchIndexApiBeanCreator.class),
            Map.entry("SuggestionSourcesApi", SuggestionSourcesApiBeanCreator.class),
            Map.entry("TeamsApi", TeamsApiBeanCreator.class),
            Map.entry("UsersApi", UsersApiBeanCreator.class)
    );

    @BuildStep
    IndexDependencyBuildItem indexUnbluApiClient() {
        return new IndexDependencyBuildItem("com.unblu.openapi", "jersey3-client-v4");
    }


    @BuildStep
    void registerUnbluApi(
            CombinedIndexBuildItem index,
            BuildProducer<SyntheticBeanBuildItem> beans
    ) {
        CLASSES.forEach((className, beanCreator) -> registerClass(index, beans, "com.unblu.webapi.jersey.v4.api." + className, beanCreator));
    }

    private static void registerClass(CombinedIndexBuildItem index, BuildProducer<SyntheticBeanBuildItem> beans, String fqcn, Class<? extends BeanCreator<Object>> beanCreatorClass) {
        if (index.getIndex().getClassByName(DotName.createSimple(fqcn)) == null) {
            return;
        }

        beans.produce(
                SyntheticBeanBuildItem.configure(DotName.createSimple(fqcn))
                        .scope(ApplicationScoped.class)
                        .unremovable()
                        .addInjectionPoint(ClassType.create(
                                DotName.createSimple("com.unblu.middleware.bootstrap.unbluapi.UnbluApiFactory")))
                        // 👇 reference a BeanCreator class that lives in *runtime*
                        .creator(beanCreatorClass)
                        .done()
        );
    }
}
