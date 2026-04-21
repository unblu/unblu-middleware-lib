package com.unblu.middleware.common.bootstrap;

import com.unblu.middleware.common.config.ApiClientConfigurer;
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
        ApiClientConfigurer.configure(apiClient, configuration);
        return apiClient;
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AccountsApi.class)
    static class AccountsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AccountsApi accountsApi(ApiClient apiClient) {
            return new AccountsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ApiKeysApi.class)
    static class ApiKeysApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ApiKeysApi apiKeysApi(ApiClient apiClient) {
            return new ApiKeysApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AuditApi.class)
    static class AuditApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AuditApi auditApi(ApiClient apiClient) {
            return new AuditApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AuthenticatorApi.class)
    static class AuthenticatorApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AuthenticatorApi authenticatorApi(ApiClient apiClient) {
            return new AuthenticatorApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AvailabilityApi.class)
    static class AvailabilityApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AvailabilityApi availabilityApi(ApiClient apiClient) {
            return new AvailabilityApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(AvatarsApi.class)
    static class AvatarsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AvatarsApi avatarsApi(ApiClient apiClient) {
            return new AvatarsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BotsApi.class)
    static class BotsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BotsApi botsApi(ApiClient apiClient) {
            return new BotsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BranchClientStatesApi.class)
    static class BranchClientStatesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BranchClientStatesApi branchClientStatesApi(ApiClient apiClient) {
            return new BranchClientStatesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BranchClientsApi.class)
    static class BranchClientsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BranchClientsApi branchClientsApi(ApiClient apiClient) {
            return new BranchClientsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(BranchesApi.class)
    static class BranchesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public BranchesApi branchesApi(ApiClient apiClient) {
            return new BranchesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CallsApi.class)
    static class CallsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CallsApi callsApi(ApiClient apiClient) {
            return new CallsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CannedResponsesApi.class)
    static class CannedResponsesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CannedResponsesApi cannedResponsesApi(ApiClient apiClient) {
            return new CannedResponsesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CollaborationLayerEventsApi.class)
    static class CollaborationLayerEventsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CollaborationLayerEventsApi collaborationLayerEventsApi(ApiClient apiClient) {
            return new CollaborationLayerEventsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CollaborationLayerLogsApi.class)
    static class CollaborationLayerLogsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CollaborationLayerLogsApi collaborationLayerLogsApi(ApiClient apiClient) {
            return new CollaborationLayerLogsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ConversationHistoryApi.class)
    static class ConversationHistoryApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ConversationHistoryApi conversationHistoryApi(ApiClient apiClient) {
            return new ConversationHistoryApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ConversationTemplatesApi.class)
    static class ConversationTemplatesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ConversationTemplatesApi conversationTemplatesApi(ApiClient apiClient) {
            return new ConversationTemplatesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ConversationsApi.class)
    static class ConversationsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ConversationsApi conversationsApi(ApiClient apiClient) {
            return new ConversationsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(CustomActionsApi.class)
    static class CustomActionsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public CustomActionsApi customActionsApi(ApiClient apiClient) {
            return new CustomActionsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(DeputyRelationshipsApi.class)
    static class DeputyRelationshipsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public DeputyRelationshipsApi deputyRelationshipsApi(ApiClient apiClient) {
            return new DeputyRelationshipsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(DomainsApi.class)
    static class DomainsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public DomainsApi domainsApi(ApiClient apiClient) {
            return new DomainsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ExternalMessengerContactsApi.class)
    static class ExternalMessengerContactsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ExternalMessengerContactsApi externalMessengerContactsApi(ApiClient apiClient) {
            return new ExternalMessengerContactsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ExternalMessengersApi.class)
    static class ExternalMessengersApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ExternalMessengersApi externalMessengersApi(ApiClient apiClient) {
            return new ExternalMessengersApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(FileUploadGlobalInterceptorsApi.class)
    static class FileUploadGlobalInterceptorsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public FileUploadGlobalInterceptorsApi fileUploadGlobalInterceptorsApi(ApiClient apiClient) {
            return new FileUploadGlobalInterceptorsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(FileUploadInterceptorsApi.class)
    static class FileUploadInterceptorsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public FileUploadInterceptorsApi fileUploadInterceptorsApi(ApiClient apiClient) {
            return new FileUploadInterceptorsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(GlobalApi.class)
    static class GlobalApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public GlobalApi globalApi(ApiClient apiClient) {
            return new GlobalApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(ImagesApi.class)
    static class ImagesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ImagesApi imagesApi(ApiClient apiClient) {
            return new ImagesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(InvitationsApi.class)
    static class InvitationsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public InvitationsApi invitationsApi(ApiClient apiClient) {
            return new InvitationsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(MessageInterceptorsApi.class)
    static class MessageInterceptorsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public MessageInterceptorsApi messageInterceptorsApi(ApiClient apiClient) {
            return new MessageInterceptorsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(NamedAreasApi.class)
    static class NamedAreasApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public NamedAreasApi namedAreasApi(ApiClient apiClient) {
            return new NamedAreasApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonLabelsApi.class)
    static class PersonLabelsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonLabelsApi personLabelsApi(ApiClient apiClient) {
            return new PersonLabelsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonPresencesApi.class)
    static class PersonPresencesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonPresencesApi personPresencesApi(ApiClient apiClient) {
            return new PersonPresencesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonVisibilityRulesApi.class)
    static class PersonVisibilityRulesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonVisibilityRulesApi personVisibilityRulesApi(ApiClient apiClient) {
            return new PersonVisibilityRulesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(PersonsApi.class)
    static class PersonsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public PersonsApi personsApi(ApiClient apiClient) {
            return new PersonsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(RecordRetentionApi.class)
    static class RecordRetentionApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public RecordRetentionApi recordRetentionApi(ApiClient apiClient) {
            return new RecordRetentionApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(SearchIndexApi.class)
    static class SearchIndexApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SearchIndexApi searchIndexApi(ApiClient apiClient) {
            return new SearchIndexApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(SuggestionSourcesApi.class)
    static class SuggestionSourcesApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SuggestionSourcesApi suggestionSourcesApi(ApiClient apiClient) {
            return new SuggestionSourcesApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(TeamsApi.class)
    static class TeamsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public TeamsApi teamsApi(ApiClient apiClient) {
            return new TeamsApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(UsersApi.class)
    static class UsersApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public UsersApi usersApi(ApiClient apiClient) {
            return new UsersApi(apiClient);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Lazy
    @ConditionalOnClass(WebhookRegistrationsApi.class)
    static class WebhookRegistrationsApiConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public WebhookRegistrationsApi webhookRegistrationsApi(ApiClient apiClient) {
            return new WebhookRegistrationsApi(apiClient);
        }
    }
}
