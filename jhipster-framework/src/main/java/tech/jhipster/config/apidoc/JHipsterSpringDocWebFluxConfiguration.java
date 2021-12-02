package tech.jhipster.config.apidoc;

import java.util.List;
import java.util.Optional;

import org.springdoc.core.AbstractRequestService;
import org.springdoc.core.ActuatorProvider;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.OperationService;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.webflux.api.MultipleOpenApiWebFluxResource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class JHipsterSpringDocWebFluxConfiguration {
    
    /** 
     * Instanticate MultipleOpenApiWebMvcResource for multiples groups
     */
    @Bean
    @Lazy(false)
    @ConditionalOnClass({
        MultipleOpenApiWebFluxResource.class,
    })
    MultipleOpenApiWebFluxResource multipleOpenApiResource(List<GroupedOpenApi> groupedOpenApis,
            ObjectFactory<OpenAPIService> defaultOpenAPIBuilder, AbstractRequestService requestBuilder,
            GenericResponseService responseBuilder, OperationService operationParser,
            SpringDocConfigProperties springDocConfigProperties,
            Optional<ActuatorProvider> actuatorProvider) {
        return new MultipleOpenApiWebFluxResource(groupedOpenApis,
                defaultOpenAPIBuilder, requestBuilder,
                responseBuilder, operationParser,
                springDocConfigProperties,
                actuatorProvider);
    }
}
