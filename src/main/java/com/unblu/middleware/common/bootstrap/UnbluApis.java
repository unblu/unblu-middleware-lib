package com.unblu.middleware.common.bootstrap;

import com.unblu.middleware.common.config.UnbluConfiguration;
import com.unblu.webapi.jersey.v4.api.*;
import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class UnbluApis {

    @Bean
    @ConditionalOnMissingBean
    public ApiClient apiClient(UnbluConfiguration configuration) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(configuration.getHost() + configuration.getApiBasePath());
        apiClient.setUsername(configuration.getUser());
        apiClient.setPassword(configuration.getPassword());

        if (configuration.getIdPropagationHeaderName() != null && configuration.getIdPropagationUserId() != null) {
            apiClient.addDefaultHeader(configuration.getIdPropagationHeaderName(), configuration.getIdPropagationUserId());
        }

        return apiClient;
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AccountsApi.class)
    public static class AccountsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AccountsApi accountsApi(ApiClient apiClient) {
            return new AccountsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ApiKeysApi.class)
    public static class ApiKeysApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ApiKeysApi apiKeysApi(ApiClient apiClient) {
            return new ApiKeysApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AuditApi.class)
    public static class AuditApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AuditApi auditApi(ApiClient apiClient) {
            return new AuditApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AuthenticatorApi.class)
    public static class AuthenticatorApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AuthenticatorApi authenticatorApi(ApiClient apiClient) {
            return new AuthenticatorApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AvailabilityApi.class)
    public static class AvailabilityApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AvailabilityApi availabilityApi(ApiClient apiClient) {
            return new AvailabilityApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AvatarsApi.class)
    public static class AvatarsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AvatarsApi avatarsApi(ApiClient apiClient) {
            return new AvatarsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BotsApi.class)
    public static class BotsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BotsApi botsApi(ApiClient apiClient) {
            return new BotsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BranchClientStatesApi.class)
    public static class BranchClientStatesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BranchClientStatesApi branchClientStatesApi(ApiClient apiClient) {
            return new BranchClientStatesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BranchClientsApi.class)
    public static class BranchClientsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BranchClientsApi branchClientsApi(ApiClient apiClient) {
            return new BranchClientsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BranchesApi.class)
    public static class BranchesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BranchesApi branchesApi(ApiClient apiClient) {
            return new BranchesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CallsApi.class)
    public static class CallsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CallsApi callsApi(ApiClient apiClient) {
            return new CallsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CannedResponsesApi.class)
    public static class CannedResponsesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CannedResponsesApi cannedResponsesApi(ApiClient apiClient) {
            return new CannedResponsesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CollaborationLayerEventsApi.class)
    public static class CollaborationLayerEventsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CollaborationLayerEventsApi collaborationLayerEventsApi(ApiClient apiClient) {
            return new CollaborationLayerEventsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CollaborationLayerLogsApi.class)
    public static class CollaborationLayerLogsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CollaborationLayerLogsApi collaborationLayerLogsApi(ApiClient apiClient) {
            return new CollaborationLayerLogsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ConversationHistoryApi.class)
    public static class ConversationHistoryApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ConversationHistoryApi conversationHistoryApi(ApiClient apiClient) {
            return new ConversationHistoryApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ConversationTemplatesApi.class)
    public static class ConversationTemplatesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ConversationTemplatesApi conversationTemplatesApi(ApiClient apiClient) {
            return new ConversationTemplatesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ConversationsApi.class)
    public static class ConversationsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ConversationsApi conversationsApi(ApiClient apiClient) {
            return new ConversationsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CustomActionsApi.class)
    public static class CustomActionsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CustomActionsApi customActionsApi(ApiClient apiClient) {
            return new CustomActionsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(DeputyRelationshipsApi.class)
    public static class DeputyRelationshipsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public DeputyRelationshipsApi deputyRelationshipsApi(ApiClient apiClient) {
            return new DeputyRelationshipsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(DomainsApi.class)
    public static class DomainsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public DomainsApi domainsApi(ApiClient apiClient) {
            return new DomainsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ExternalMessengerContactsApi.class)
    public static class ExternalMessengerContactsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ExternalMessengerContactsApi externalMessengerContactsApi(ApiClient apiClient) {
            return new ExternalMessengerContactsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ExternalMessengersApi.class)
    public static class ExternalMessengersApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ExternalMessengersApi externalMessengersApi(ApiClient apiClient) {
            return new ExternalMessengersApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(FileUploadGlobalInterceptorsApi.class)
    public static class FileUploadGlobalInterceptorsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public FileUploadGlobalInterceptorsApi fileUploadGlobalInterceptorsApi(ApiClient apiClient) {
            return new FileUploadGlobalInterceptorsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(FileUploadInterceptorsApi.class)
    public static class FileUploadInterceptorsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public FileUploadInterceptorsApi fileUploadInterceptorsApi(ApiClient apiClient) {
            return new FileUploadInterceptorsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(GlobalApi.class)
    public static class GlobalApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public GlobalApi globalApi(ApiClient apiClient) {
            return new GlobalApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ImagesApi.class)
    public static class ImagesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ImagesApi imagesApi(ApiClient apiClient) {
            return new ImagesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(InvitationsApi.class)
    public static class InvitationsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public InvitationsApi invitationsApi(ApiClient apiClient) {
            return new InvitationsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(MessageInterceptorsApi.class)
    public static class MessageInterceptorsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public MessageInterceptorsApi messageInterceptorsApi(ApiClient apiClient) {
            return new MessageInterceptorsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(NamedAreasApi.class)
    public static class NamedAreasApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public NamedAreasApi namedAreasApi(ApiClient apiClient) {
            return new NamedAreasApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonLabelsApi.class)
    public static class PersonLabelsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonLabelsApi personLabelsApi(ApiClient apiClient) {
            return new PersonLabelsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonPresencesApi.class)
    public static class PersonPresencesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonPresencesApi personPresencesApi(ApiClient apiClient) {
            return new PersonPresencesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonVisibilityRulesApi.class)
    public static class PersonVisibilityRulesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonVisibilityRulesApi personVisibilityRulesApi(ApiClient apiClient) {
            return new PersonVisibilityRulesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonsApi.class)
    public static class PersonsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonsApi personsApi(ApiClient apiClient) {
            return new PersonsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(RecordRetentionApi.class)
    public static class RecordRetentionApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public RecordRetentionApi recordRetentionApi(ApiClient apiClient) {
            return new RecordRetentionApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(SearchIndexApi.class)
    public static class SearchIndexApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SearchIndexApi searchIndexApi(ApiClient apiClient) {
            return new SearchIndexApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(SuggestionSourcesApi.class)
    public static class SuggestionSourcesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SuggestionSourcesApi suggestionSourcesApi(ApiClient apiClient) {
            return new SuggestionSourcesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(TeamsApi.class)
    public static class TeamsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public TeamsApi teamsApi(ApiClient apiClient) {
            return new TeamsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(UsersApi.class)
    public static class UsersApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public UsersApi usersApi(ApiClient apiClient) {
            return new UsersApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(WebhookRegistrationsApi.class)
    public static class WebhookRegistrationsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public WebhookRegistrationsApi webhookRegistrationsApi(ApiClient apiClient) {
            return new WebhookRegistrationsApi(apiClient);
        }
    }
}
