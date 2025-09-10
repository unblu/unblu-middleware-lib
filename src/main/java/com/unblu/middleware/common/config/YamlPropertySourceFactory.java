package com.unblu.middleware.common.config;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Optional;
import java.util.Properties;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.beans.factory.config.YamlProcessor.MatchStatus.*;

@Slf4j
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    @NonNull
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        String activeProfile = Optional.ofNullable(System.getenv("SPRING_PROFILES_ACTIVE"))
                .orElseGet(() -> Optional.ofNullable(System.getProperty("spring.profiles.active"))
                        .orElse("dev"));

        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setDocumentMatchers(properties ->
                Optional.ofNullable(emptyToNull(properties.getProperty("spring.profiles")))
                        .map(it -> it.contains(activeProfile) ? FOUND : NOT_FOUND)
                        .orElse(ABSTAIN));

        yamlFactory.setResources(encodedResource.getResource());

        Properties properties = yamlFactory.getObject();

        assert properties != null;
        return new PropertiesPropertySource(requireNonNull(encodedResource.getResource().getFilename()), properties);
    }
}
