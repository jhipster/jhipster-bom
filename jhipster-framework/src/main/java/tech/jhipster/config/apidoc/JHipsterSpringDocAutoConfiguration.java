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

import io.swagger.v3.oas.models.OpenAPI;

import org.springdoc.core.configuration.SpringDocConfiguration;
import tech.jhipster.config.JHipsterProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import static tech.jhipster.config.JHipsterConstants.SPRING_PROFILE_API_DOCS;

/**
 * OpenAPI configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, OpenApi can become a performance issue.
 * In that case, you can use the "no-api-docs" Spring profile, so that this bean is ignored.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(OpenAPI.class)
@Profile(SPRING_PROFILE_API_DOCS)
@AutoConfigureBefore(SpringDocConfiguration.class)
@AutoConfigureAfter(JHipsterProperties.class)
@Import(JHipsterSpringDocGroupsConfiguration.class)
public class JHipsterSpringDocAutoConfiguration {}
