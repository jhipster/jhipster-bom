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

package tech.jhipster.config.apidoc.customizer;

import tech.jhipster.config.JHipsterProperties;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.springdoc.core.customizers.OpenApiCustomizer;

import org.springframework.core.Ordered;

/**
 * A OpenApi customizer to set up {@link io.swagger.v3.oas.models.OpenAPI} with JHipster settings.
 */
public class JHipsterOpenApiCustomizer implements OpenApiCustomizer, Ordered {

    /**
     * The default order for the customizer.
     */
    public static final int DEFAULT_ORDER = 0;

    private int order = DEFAULT_ORDER;

    private final JHipsterProperties.ApiDocs properties;

    /**
     * <p>Constructor for JHipsterOpenApiCustomizer.</p>
     *
     * @param properties a {@link JHipsterProperties.ApiDocs} object.
     */
    public JHipsterOpenApiCustomizer(JHipsterProperties.ApiDocs properties) {
        this.properties = properties;
    }

    /** {@inheritDoc} */
    @Override
    public void customise(OpenAPI openAPI) {
        Contact contact = new Contact()
            .name(properties.getContactName())
            .url(properties.getContactUrl())
            .email(properties.getContactEmail());

        openAPI.info(new Info()
            .contact(contact)
            .title(properties.getTitle())
            .description(properties.getDescription())
            .version(properties.getVersion())
            .termsOfService(properties.getTermsOfServiceUrl())
            .license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()))
        );

        for (JHipsterProperties.ApiDocs.Server server : properties.getServers()) {
            openAPI.addServersItem(new Server().url(server.getUrl()).description(server.getDescription()));
        }
    }

    /**
     * <p>Setter for the field <code>order</code>.</p>
     *
     * @param order a int.
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /** {@inheritDoc} */
    @Override
    public int getOrder() {
        return order;
    }
}
