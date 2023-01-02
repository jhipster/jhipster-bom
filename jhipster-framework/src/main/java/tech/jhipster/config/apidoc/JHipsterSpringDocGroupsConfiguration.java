/*
 * Copyright 2016-2023 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.jhipster.config.apidoc;

import io.swagger.v3.oas.models.info.Info;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.ActuatorOpenApiCustomizer;
import org.springdoc.core.customizers.ActuatorOperationCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.apidoc.customizer.JHipsterOpenApiCustomizer;

import static org.springdoc.core.utils.Constants.DEFAULT_GROUP_NAME;
import static org.springdoc.core.utils.Constants.SPRINGDOC_SHOW_ACTUATOR;
import static org.springdoc.core.utils.SpringDocUtils.getConfig;

/**
 * OpenApi Groups configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, OpenApi can become a performance issue.
 * In that case, you can use the "no-api-docs" Spring profile, so that this bean is ignored.
 */
@Configuration
public class JHipsterSpringDocGroupsConfiguration {

    public static final String MANAGEMENT_GROUP_NAME = "management";

    static {
        getConfig().replaceWithClass(ByteBuffer.class, String.class);
    }

    static final String MANAGEMENT_TITLE_SUFFIX = "Management API";
    static final String MANAGEMENT_DESCRIPTION = "Management endpoints documentation";

    private final Logger log = LoggerFactory.getLogger(JHipsterSpringDocGroupsConfiguration.class);

    private final JHipsterProperties.ApiDocs properties;

    /**
     * <p>Constructor for OpenApiAutoConfiguration.</p>
     *
     * @param jHipsterProperties a {@link JHipsterProperties} object.
     */
    public JHipsterSpringDocGroupsConfiguration(JHipsterProperties jHipsterProperties) {
        properties = jHipsterProperties.getApiDocs();
    }

    /**
     * JHipster OpenApi Customiser
     *
     * @return the Customizer of JHipster
     */
    @Bean
    public JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer() {
        log.debug("Initializing JHipster OpenApi customizer");
        return new JHipsterOpenApiCustomizer(properties);
    }

    /**
     * OpenApi default group configuration.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    @ConditionalOnMissingBean(name = "openAPIDefaultGroupedOpenAPI")
    public GroupedOpenApi openAPIDefaultGroupedOpenAPI(
        List<OpenApiCustomizer> openApiCustomizers,
        List<OperationCustomizer> operationCustomizers,
        @Qualifier("apiFirstGroupedOpenAPI") Optional<GroupedOpenApi> apiFirstGroupedOpenAPI) {
        log.debug("Initializing JHipster OpenApi default group");
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
            .group(DEFAULT_GROUP_NAME)
            .pathsToMatch(properties.getDefaultIncludePattern());
        openApiCustomizers.stream()
            .filter(customizer -> !(customizer instanceof ActuatorOpenApiCustomizer))
            .forEach(builder::addOpenApiCustomizer);
        operationCustomizers.stream()
            .filter(customizer -> !(customizer instanceof ActuatorOperationCustomizer))
            .forEach(builder::addOperationCustomizer);
        apiFirstGroupedOpenAPI.map(GroupedOpenApi::getPackagesToScan)
            .ifPresent(packagesToScan -> packagesToScan.forEach(builder::packagesToExclude));
        return builder.build();
    }

    /**
     * OpenApi management group configuration for the management endpoints (actuator) OpenAPI docs.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties")
    @ConditionalOnMissingBean(name = "openAPIManagementGroupedOpenAPI")
    @ConditionalOnProperty(SPRINGDOC_SHOW_ACTUATOR)
    public GroupedOpenApi openAPIManagementGroupedOpenAPI(
        @Value("${spring.application.name:application}") String appName
    ) {
        log.debug("Initializing JHipster OpenApi management group");
        return GroupedOpenApi.builder()
            .group(MANAGEMENT_GROUP_NAME)
            .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                .title(StringUtils.capitalize(appName) + " " + MANAGEMENT_TITLE_SUFFIX)
                .description(MANAGEMENT_DESCRIPTION)
                .version(properties.getVersion())
            ))
            .pathsToMatch(properties.getManagementIncludePattern())
            .build();
    }
}
