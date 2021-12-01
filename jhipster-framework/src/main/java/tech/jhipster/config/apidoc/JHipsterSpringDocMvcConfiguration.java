package tech.jhipster.config.apidoc;

import java.util.List;
import java.util.Optional;

import org.springdoc.core.AbstractRequestService;
import org.springdoc.core.ActuatorProvider;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.OperationService;
import org.springdoc.core.RepositoryRestResourceProvider;
import org.springdoc.core.SecurityOAuth2Provider;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource;
import org.springdoc.webmvc.core.RouterFunctionProvider;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JHipsterSpringDocMvcConfiguration {

    /** 
     * Instanticate MultipleOpenApiWebMvcResource for multiples groups
     */
    @Bean
    @Lazy(false)
    @ConditionalOnClass({
    	MultipleOpenApiWebMvcResource.class,
    })
    MultipleOpenApiWebMvcResource multipleOpenApiResource(List<GroupedOpenApi> groupedOpenApis,
            ObjectFactory<OpenAPIService> defaultOpenAPIBuilder, AbstractRequestService requestBuilder,
            GenericResponseService responseBuilder, OperationService operationParser,
            Optional<ActuatorProvider> actuatorProvider,
            SpringDocConfigProperties springDocConfigProperties,
            Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider,
            Optional<RouterFunctionProvider> routerFunctionProvider,
            Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider) {
        return new MultipleOpenApiWebMvcResource(groupedOpenApis,
                defaultOpenAPIBuilder, requestBuilder,
                responseBuilder, operationParser,
                actuatorProvider,
                springDocConfigProperties,
                springSecurityOAuth2Provider,
                routerFunctionProvider, repositoryRestResourceProvider);
    }
}
