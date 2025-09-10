package com.unblu.middleware.common.bootstrap;

import com.unblu.middleware.common.config.UnbluConfiguration;
import com.unblu.webapi.jersey.v4.api.*;
import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
@Lazy
public class UnbluApis {

    private final UnbluConfiguration configuration;

    @Bean
    public ApiClient apiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(configuration.getHost() + configuration.getApiBasePath());
        apiClient.setUsername(configuration.getUser());
        apiClient.setPassword(configuration.getPassword());

        if (configuration.getIdPropagationHeaderName() != null && configuration.getIdPropagationUserId() != null) {
            apiClient.addDefaultHeader(configuration.getIdPropagationHeaderName(), configuration.getIdPropagationUserId());
        }

        return apiClient;
    }

    @Bean
    public AccountsApi accountsApi(ApiClient apiClient) {
        return new AccountsApi(apiClient);
    }

    @Bean
    public ApiKeysApi apiKeysApi(ApiClient apiClient) {
        return new ApiKeysApi(apiClient);
    }

    @Bean
    public AuditApi auditApi(ApiClient apiClient) {
        return new AuditApi(apiClient);
    }

    @Bean
    public AuthenticatorApi authenticatorApi(ApiClient apiClient) {
        return new AuthenticatorApi(apiClient);
    }

    @Bean
    public AvailabilityApi availabilityApi(ApiClient apiClient) {
        return new AvailabilityApi(apiClient);
    }

    @Bean
    public AvatarsApi avatarsApi(ApiClient apiClient) {
        return new AvatarsApi(apiClient);
    }

    @Bean
    public BotsApi botsApi(ApiClient apiClient) {
        return new BotsApi(apiClient);
    }

    @Bean
    public BranchClientStatesApi branchClientStatesApi(ApiClient apiClient) {
        return new BranchClientStatesApi(apiClient);
    }

    @Bean
    public BranchClientsApi branchClientsApi(ApiClient apiClient) {
        return new BranchClientsApi(apiClient);
    }

    @Bean
    public BranchesApi branchesApi(ApiClient apiClient) {
        return new BranchesApi(apiClient);
    }

    @Bean
    public CallsApi callsApi(ApiClient apiClient) {
        return new CallsApi(apiClient);
    }

    @Bean
    public CannedResponsesApi cannedResponsesApi(ApiClient apiClient) {
        return new CannedResponsesApi(apiClient);
    }

    @Bean
    public CollaborationLayerEventsApi collaborationLayerEventsApi(ApiClient apiClient) {
        return new CollaborationLayerEventsApi(apiClient);
    }

    @Bean
    public CollaborationLayerLogsApi collaborationLayerLogsApi(ApiClient apiClient) {
        return new CollaborationLayerLogsApi(apiClient);
    }

    @Bean
    public ConversationHistoryApi conversationHistoryApi(ApiClient apiClient) {
        return new ConversationHistoryApi(apiClient);
    }

    @Bean
    public ConversationTemplatesApi conversationTemplatesApi(ApiClient apiClient) {
        return new ConversationTemplatesApi(apiClient);
    }

    @Bean
    public ConversationsApi conversationsApi(ApiClient apiClient) {
        return new ConversationsApi(apiClient);
    }

    @Bean
    public CustomActionsApi customActionsApi(ApiClient apiClient) {
        return new CustomActionsApi(apiClient);
    }

    @Bean
    public DeputyRelationshipsApi deputyRelationshipsApi(ApiClient apiClient) {
        return new DeputyRelationshipsApi(apiClient);
    }

    @Bean
    public DomainsApi domainsApi(ApiClient apiClient) {
        return new DomainsApi(apiClient);
    }

    @Bean
    public ExternalMessengerContactsApi externalMessengerContactsApi(ApiClient apiClient) {
        return new ExternalMessengerContactsApi(apiClient);
    }

    @Bean
    public ExternalMessengersApi externalMessengersApi(ApiClient apiClient) {
        return new ExternalMessengersApi(apiClient);
    }

    @Bean
    public FileUploadGlobalInterceptorsApi fileUploadGlobalInterceptorsApi(ApiClient apiClient) {
        return new FileUploadGlobalInterceptorsApi(apiClient);
    }

    @Bean
    public FileUploadInterceptorsApi fileUploadInterceptorsApi(ApiClient apiClient) {
        return new FileUploadInterceptorsApi(apiClient);
    }

    @Bean
    public GlobalApi globalApi(ApiClient apiClient) {
        return new GlobalApi(apiClient);
    }

    @Bean
    public ImagesApi imagesApi(ApiClient apiClient) {
        return new ImagesApi(apiClient);
    }

    @Bean
    public InvitationsApi invitationsApi(ApiClient apiClient) {
        return new InvitationsApi(apiClient);
    }

    @Bean
    public MessageInterceptorsApi messageInterceptorsApi(ApiClient apiClient) {
        return new MessageInterceptorsApi(apiClient);
    }

    @Bean
    public NamedAreasApi namedAreasApi(ApiClient apiClient) {
        return new NamedAreasApi(apiClient);
    }

    @Bean
    public PersonLabelsApi personLabelsApi(ApiClient apiClient) {
        return new PersonLabelsApi(apiClient);
    }

    @Bean
    public PersonPresencesApi personPresencesApi(ApiClient apiClient) {
        return new PersonPresencesApi(apiClient);
    }

    @Bean
    public PersonVisibilityRulesApi personVisibilityRulesApi(ApiClient apiClient) {
        return new PersonVisibilityRulesApi(apiClient);
    }

    @Bean
    public PersonsApi personsApi(ApiClient apiClient) {
        return new PersonsApi(apiClient);
    }

    @Bean
    public RecordRetentionApi recordRetentionApi(ApiClient apiClient) {
        return new RecordRetentionApi(apiClient);
    }

    @Bean
    public SearchIndexApi searchIndexApi(ApiClient apiClient) {
        return new SearchIndexApi(apiClient);
    }

    @Bean
    public SuggestionSourcesApi suggestionSourcesApi(ApiClient apiClient) {
        return new SuggestionSourcesApi(apiClient);
    }

    @Bean
    public TeamsApi teamsApi(ApiClient apiClient) {
        return new TeamsApi(apiClient);
    }

    @Bean
    public UsersApi usersApi(ApiClient apiClient) {
        return new UsersApi(apiClient);
    }

    @Bean
    public WebhookRegistrationsApi webhookRegistrationsApi(ApiClient apiClient) {
        return new WebhookRegistrationsApi(apiClient);
    }
}
