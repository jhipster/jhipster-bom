package tech.jhipster.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Configure the usage of JHipsterProperties.
 */
@EnableConfigurationProperties(JHipsterProperties.class)
// Load some properties into the environment from files to make them available for interpolation in application.yaml.
@PropertySources({
    @PropertySource(value = "classpath:git.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:META-INF/build-info.properties", ignoreResourceNotFound = true)
})
public class JHipsterConfiguration {
}
