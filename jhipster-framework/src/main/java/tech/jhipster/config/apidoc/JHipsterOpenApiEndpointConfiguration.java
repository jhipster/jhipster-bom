package tech.jhipster.config.apidoc;

import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>JHipsterOpenApiEndpointConfiguration class.</p>
 */
@Configuration
@ConditionalOnClass(SpringDocConfigProperties.class)
@AutoConfigureAfter(JHipsterSpringDocAutoConfiguration.class)
public class JHipsterOpenApiEndpointConfiguration {

    /**
     * <p>jHipsterOpenApiEndpoint.</p>
     *
     * @param springDocConfigProperties a {@link org.springdoc.core.SpringDocConfigProperties} object.
     * @return a {@link JHipsterOpenApiEndpoint} object.
     */
    @Bean
    @ConditionalOnBean({SpringDocConfigProperties.class})
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public JHipsterOpenApiEndpoint jHipsterOpenApiEndpoint(
            SpringDocConfigProperties springDocConfigProperties,
            @Value("${spring.application.name:application}") String appName
            ) {
        return new JHipsterOpenApiEndpoint(springDocConfigProperties, appName);
    }
}
