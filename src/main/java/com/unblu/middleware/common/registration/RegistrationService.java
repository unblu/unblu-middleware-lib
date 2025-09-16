package com.unblu.middleware.common.registration;

import com.unblu.middleware.common.automation.AutoRegistrable;
import com.unblu.middleware.common.automation.SelfHealing;
import com.unblu.middleware.common.error.RegistrationException;
import com.unblu.middleware.common.utils.ThrowingConsumer;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;

import static com.unblu.middleware.common.utils.ObjectUtils.copyOf;

@Slf4j
@RequiredArgsConstructor
public abstract class RegistrationService<T> implements SelfHealing, AutoRegistrable {

    private final RegistrationConfiguration registrationConfiguration;
    private boolean hasAutoRegistered = false;

    @Override
    public void autoRegister() {
        reconcile();
        hasAutoRegistered = true;
    }

    protected boolean hasAutoRegistered() {
        return hasAutoRegistered;
    }

    @Override
    public void selfHeal() {
        reconcile(false);
    }

    protected void reconcile() {
        reconcile(registrationConfiguration.shouldCleanPrevious());
    }

    protected void reconcile(boolean shouldCleanPrevious) {
        if (shouldCleanPrevious) {
            deleteRegistration();
            createNewRegistration();
        } else {
            getRegistration()
                    .ifPresentOrElse(
                            this::updateRegistration,
                            this::createNewRegistration
                    );
        }
    }

    public void deleteRegistration() {
        getRegistration()
                .ifPresent(
                        registration -> {
                            try {
                                log.info("Deleting existing registration '{}'", getRegistrationName());
                                callDeleteRegistration(registration);
                            } catch (ApiException e) {
                                if (e.getCode() == 404) {
                                    log.info("No existing registration '{}' found, continuing", getRegistrationName());
                                } else {
                                    throw error(e);
                                }
                            }
                        }
                );
    }

    public void createNewRegistration() {
        applyConfiguration(emptyConfiguration())
                .ifPresent(wrapException(c -> {
                    log.info("Creating new '{}' registration", getRegistrationName());
                    callCreateNewRegistration(c);
                }));
    }

    public void updateRegistration(T originalRegistration) {
        applyConfiguration(copyOf(originalRegistration))
                .ifPresentOrElse(
                        wrapException(newRegistration -> updateRegistrationIfChanged(originalRegistration, newRegistration)),
                        this::deleteRegistration);
    }

    private void updateRegistrationIfChanged(T originalRegistration, T newRegistration) throws ApiException {
        if (!newRegistration.equals(originalRegistration)) {
            log.info("Registration '{}' has changed, updating", getRegistrationName());
            callUpdateRegistration(newRegistration);
        }
    }

    public Optional<T> getRegistration() {
        try {
            return Optional.ofNullable(callGetRegistration(getRegistrationName()));
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return Optional.empty();
            } else {
                throw error(e);
            }
        }
    }

    protected abstract void callCreateNewRegistration(T registration) throws ApiException;

    protected abstract void callUpdateRegistration(T registration) throws ApiException;

    protected abstract void callDeleteRegistration(T registration) throws ApiException;

    protected abstract T callGetRegistration(String registrationName) throws ApiException;

    protected abstract T emptyConfiguration();

    protected abstract Optional<T> applyConfiguration(T registration);

    protected String getRegistrationName() {
        return registrationConfiguration.registrationName();
    }

    private RegistrationException error(Throwable e) {
        var message = "Error during webhook registration management for '" + getRegistrationName() + "': " + e.getMessage();
        log.error(message);
        return new RegistrationException(message, e);
    }

    private <P> Consumer<P> wrapException(ThrowingConsumer<P> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw error(e);
            }
        };
    }


    @PreDestroy
    public void stop() {
        if (registrationConfiguration.shouldCleanPrevious()) {
            try {
                deleteRegistration();
            } catch (Exception e) {
                // well, we tried
                log.error("Error deleting registration during shutdown '{}'.", getRegistrationName(), e);
            }
        }
    }
}
