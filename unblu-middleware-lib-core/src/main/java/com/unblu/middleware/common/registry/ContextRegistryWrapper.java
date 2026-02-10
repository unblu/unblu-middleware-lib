package com.unblu.middleware.common.registry;

import com.unblu.middleware.common.entity.ContextSpec;
import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import reactor.core.publisher.Hooks;

@RequiredArgsConstructor
@Named
@Singleton
public class ContextRegistryWrapper {

    private final ContextRegistry contextRegistry = ContextRegistry.getInstance();

    @PostConstruct
    public void init() {
        Hooks.enableAutomaticContextPropagation();
    }

    public <T> void registerContextSpec(ContextSpec<T> contextSpec) {
        contextSpec.contextEntries().forEach((key, value) ->
                contextRegistry.registerThreadLocalAccessor(
                        key,
                        () -> MDC.get(key),
                        v -> MDC.put(key, v),
                        () -> MDC.remove(key))
        );
    }
}
