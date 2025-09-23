package com.unblu.middleware.common.registry;

import com.unblu.middleware.common.entity.ContextEntries;
import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.util.context.Context;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class ContextRegistryWrapper {

    private final ContextRegistry contextRegistry;

    @PostConstruct
    public void init() {
        Hooks.enableAutomaticContextPropagation();
    }

    public static <T> Context requestContext(Request<T> request, ContextEntries<Request<T>> contextEntries) {
        return Context.of(
                contextEntries.contextEntries().stream().collect(Collectors.toMap(
                        ContextEntrySpec::key,
                        entry -> entry.valueExtractor().apply(request)
                )));
    }

    public  <T> void registerContextEntries(Collection<ContextEntrySpec<T>> contextEntries) {
        contextEntries.forEach(contextEntry ->
                contextRegistry.registerThreadLocalAccessor(
                        contextEntry.key(),
                        () -> MDC.get(contextEntry.key()),
                        v -> MDC.put(contextEntry.key(), v),
                        () -> MDC.remove(contextEntry.key())));
    }
}
