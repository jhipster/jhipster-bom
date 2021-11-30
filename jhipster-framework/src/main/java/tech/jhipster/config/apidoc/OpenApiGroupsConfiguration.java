/*
 * Copyright 2016-2021 the original author or authors from the JHipster project.
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

import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.apidoc.customizer.JHipsterOpenApiCustomizer;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.converters.PageableOpenAPIConverter;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import io.swagger.v3.oas.models.info.Info;

import static org.springdoc.core.Constants.SPRINGDOC_SHOW_ACTUATOR;
import static org.springdoc.core.Constants.DEFAULT_GROUP_NAME;

import static org.springdoc.core.SpringDocUtils.getConfig;

import java.nio.ByteBuffer;

/**
 * OpenApi Groups configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, OpenApi can become a performance issue.
 * In that case, you can use the "no-api-docs" Spring profile, so that this bean is ignored.
 */
@Configuration
public class OpenApiGroupsConfiguration {

    static {
        /** 
         * Add support to `@ParamObject Pageable`
         */
        getConfig().replaceParameterObjectWithClass(org.springframework.data.domain.Pageable.class, Pageable.class)
            .replaceParameterObjectWithClass(org.springframework.data.domain.PageRequest.class, Pageable.class)
            .replaceWithClass(ByteBuffer.class, String.class);
    }

    static final String STARTING_MESSAGE = "Starting OpenAPI docs";
    static final String STARTED_MESSAGE = "Started OpenAPI docs in {} ms";
    static final String MANAGEMENT_TITLE_SUFFIX = "Management API";
    static final String MANAGEMENT_GROUP_NAME = "management";
    static final String MANAGEMENT_DESCRIPTION = "Management endpoints documentation";

    private final JHipsterProperties.ApiDocs properties;

    /**
     * <p>Constructor for OpenApiAutoConfiguration.</p>
     *
     * @param jHipsterProperties a {@link JHipsterProperties} object.
     */
    public OpenApiGroupsConfiguration(JHipsterProperties jHipsterProperties) {
        this.properties = jHipsterProperties.getApiDocs();
    }

    /** 
     * Add support to `@PageableAsQueryParam`
     */
    @Bean
	@ConditionalOnMissingBean
	@Lazy(false)
	PageableOpenAPIConverter pageableOpenAPIConverter() {
		return new PageableOpenAPIConverter();
	}

    /**
     * JHipster OpenApi Customiser
     *
     * @return the Customizer of JHipster
     */
    @Bean
    public JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer() {
        return new JHipsterOpenApiCustomizer(properties);
    }

    /**
     * OpenApi default group configuration.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    @ConditionalOnMissingBean(name = "openAPIDefaultGroupedOpenAPI")
    public GroupedOpenApi openAPIDefaultGroupedOpenAPI(JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer) {
        return GroupedOpenApi.builder()
            .group(DEFAULT_GROUP_NAME)
            .addOpenApiCustomiser(jhipsterOpenApiCustomizer)
            .pathsToMatch(properties.getDefaultIncludePattern())
            .build();
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
    public GroupedOpenApi openAPIManagementGroupedOpenAPI(@Value("${spring.application.name:application}") String appName) {
        return GroupedOpenApi.builder()
            .group(MANAGEMENT_GROUP_NAME)
            .addOpenApiCustomiser(openApi -> {
                openApi.info(new Info()
                    .title(StringUtils.capitalize(appName) + " " + MANAGEMENT_TITLE_SUFFIX)
                    .description(MANAGEMENT_DESCRIPTION)
                    .version(properties.getVersion())
                );
            })
            .pathsToMatch(properties.getManagementIncludePattern())
            .build();
    }
}
