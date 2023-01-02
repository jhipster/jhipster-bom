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

import org.springdoc.core.properties.SpringDocConfigProperties;
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
     * @param springDocConfigProperties a {@link org.springdoc.core.properties.SpringDocConfigProperties} object.
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
