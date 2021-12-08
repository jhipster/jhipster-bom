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

import static org.springdoc.core.Constants.DEFAULT_GROUP_NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfigProperties.GroupConfig;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

/**
 * <p>JHipsterOpenApiEndpoint class.</p>
 */
@WebEndpoint(id = "jhiopenapigroups")
public class JHipsterOpenApiEndpoint {

    private final SpringDocConfigProperties springDocConfigProperties;
    private final String appName;

    /**
     * <p>Constructor for JHipsterOpenApiEndpoint.</p>
     *
     * @param springDocConfigProperties a {@link org.springdoc.core.SpringDocConfigProperties} object.
     */
    public JHipsterOpenApiEndpoint(SpringDocConfigProperties springDocConfigProperties, String appName) {
        this.springDocConfigProperties = springDocConfigProperties;
        this.appName = appName;
    }

    /**
     * GET /management/jhiopenapigroups
     * <p>
     * Give openApi displayed on OpenApi page
     *
     * @return a Map with a String defining a category of openApi as Key and
     * another Map containing openApi related to this category as Value
     */
    @ReadOperation
    public List<Map<String, String>> allOpenApi() {
        return springDocConfigProperties.getGroupConfigs().stream().map(this::createGroupMap).collect(Collectors.toList());
    }

    private Map<String, String> createGroupMap(GroupConfig group) {
        Map<String, String> map = new HashMap<String, String>();
        String groupName = group.getGroup();
        map.put("group", groupName);
        String description = this.appName + " (" + (groupName == DEFAULT_GROUP_NAME ? "default" : groupName) + ")";
        map.put("description", description);
        return map;
    }
}
