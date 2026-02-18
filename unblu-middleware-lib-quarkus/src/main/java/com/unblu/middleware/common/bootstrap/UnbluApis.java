package com.unblu.middleware.common.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.config.UnbluConfiguration;
import com.unblu.middleware.common.utils.ObjectUtils;
import com.unblu.webapi.jersey.v4.api.*;
import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

/**
 * CDI producer for Unblu API client and all API instances.
 * All APIs are produced as @ApplicationScoped beans that can be injected.
 */
@ApplicationScoped
public class UnbluApis {

    @Inject
    UnbluConfiguration configuration;

    @Produces
    @ApplicationScoped
    public ObjectMapper objectMapper() {
        return ObjectUtils.getObjectMapper();
    }

    @Produces
    @ApplicationScoped
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

    @Produces
    @ApplicationScoped
    public AccountsApi accountsApi(ApiClient apiClient) {
        return new AccountsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public ApiKeysApi apiKeysApi(ApiClient apiClient) {
        return new ApiKeysApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public AuditApi auditApi(ApiClient apiClient) {
        return new AuditApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public AuthenticatorApi authenticatorApi(ApiClient apiClient) {
        return new AuthenticatorApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public AvailabilityApi availabilityApi(ApiClient apiClient) {
        return new AvailabilityApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public AvatarsApi avatarsApi(ApiClient apiClient) {
        return new AvatarsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public BotsApi botsApi(ApiClient apiClient) {
        return new BotsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public BranchClientStatesApi branchClientStatesApi(ApiClient apiClient) {
        return new BranchClientStatesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public BranchClientsApi branchClientsApi(ApiClient apiClient) {
        return new BranchClientsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public BranchesApi branchesApi(ApiClient apiClient) {
        return new BranchesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public CallsApi callsApi(ApiClient apiClient) {
        return new CallsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public CannedResponsesApi cannedResponsesApi(ApiClient apiClient) {
        return new CannedResponsesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public CollaborationLayerEventsApi collaborationLayerEventsApi(ApiClient apiClient) {
        return new CollaborationLayerEventsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public CollaborationLayerLogsApi collaborationLayerLogsApi(ApiClient apiClient) {
        return new CollaborationLayerLogsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public ConversationHistoryApi conversationHistoryApi(ApiClient apiClient) {
        return new ConversationHistoryApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public ConversationTemplatesApi conversationTemplatesApi(ApiClient apiClient) {
        return new ConversationTemplatesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public ConversationsApi conversationsApi(ApiClient apiClient) {
        return new ConversationsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public CustomActionsApi customActionsApi(ApiClient apiClient) {
        return new CustomActionsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public DeputyRelationshipsApi deputyRelationshipsApi(ApiClient apiClient) {
        return new DeputyRelationshipsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public DevicesApi devicesApi(ApiClient apiClient) {
        return new DevicesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public DialogBotsApi dialogBotsApi(ApiClient apiClient) {
        return new DialogBotsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public ExternalMessengersApi externalMessengersApi(ApiClient apiClient) {
        return new ExternalMessengersApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public FileDownloadInterceptorsApi fileDownloadInterceptorsApi(ApiClient apiClient) {
        return new FileDownloadInterceptorsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public FileUploadInterceptorsApi fileUploadInterceptorsApi(ApiClient apiClient) {
        return new FileUploadInterceptorsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public FilesApi filesApi(ApiClient apiClient) {
        return new FilesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public GlobalApi globalApi(ApiClient apiClient) {
        return new GlobalApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public InboundRequestsApi inboundRequestsApi(ApiClient apiClient) {
        return new InboundRequestsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public InitialEngagementTypesApi initialEngagementTypesApi(ApiClient apiClient) {
        return new InitialEngagementTypesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public InterceptorPromptsApi interceptorPromptsApi(ApiClient apiClient) {
        return new InterceptorPromptsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public InvitationsApi invitationsApi(ApiClient apiClient) {
        return new InvitationsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public LabelsApi labelsApi(ApiClient apiClient) {
        return new LabelsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public MessagesApi messagesApi(ApiClient apiClient) {
        return new MessagesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public MobileAppPushNotificationApi mobileAppPushNotificationApi(ApiClient apiClient) {
        return new MobileAppPushNotificationApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public MobileAuthenticationSecretResetApi mobileAuthenticationSecretResetApi(ApiClient apiClient) {
        return new MobileAuthenticationSecretResetApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public NamedAreasApi namedAreasApi(ApiClient apiClient) {
        return new NamedAreasApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public OutboundRequestsApi outboundRequestsApi(ApiClient apiClient) {
        return new OutboundRequestsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public ParticipantsApi participantsApi(ApiClient apiClient) {
        return new ParticipantsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public PersonLabelsApi personLabelsApi(ApiClient apiClient) {
        return new PersonLabelsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public PersonsApi personsApi(ApiClient apiClient) {
        return new PersonsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public PresenceApi presenceApi(ApiClient apiClient) {
        return new PresenceApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public QueueOrderApi queueOrderApi(ApiClient apiClient) {
        return new QueueOrderApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public QueuesApi queuesApi(ApiClient apiClient) {
        return new QueuesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public RatingsApi ratingsApi(ApiClient apiClient) {
        return new RatingsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public SecondaryConversationStartersApi secondaryConversationStartersApi(ApiClient apiClient) {
        return new SecondaryConversationStartersApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public TeamsApi teamsApi(ApiClient apiClient) {
        return new TeamsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public TextModulesApi textModulesApi(ApiClient apiClient) {
        return new TextModulesApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public TokensApi tokensApi(ApiClient apiClient) {
        return new TokensApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public TranslationsApi translationsApi(ApiClient apiClient) {
        return new TranslationsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public UsersApi usersApi(ApiClient apiClient) {
        return new UsersApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public WebhooksApi webhooksApi(ApiClient apiClient) {
        return new WebhooksApi(apiClient);
    }
}

