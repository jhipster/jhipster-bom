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

package tech.jhipster.config.info;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockEnvironment;

class ActiveProfilesInfoContributorTest {

    @Test
    void activeProfilesShouldBeSetWhenProfilesActivated() {
        ConfigurableEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");
        environment.setDefaultProfiles("dev", "api-docs");

        ActiveProfilesInfoContributor contributor = new ActiveProfilesInfoContributor(environment);

        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        assertThat(info.get("activeProfiles")).asInstanceOf(list(String.class)).contains("prod");
    }

    @Test
    void defaultProfilesShouldBeSetWhenNoProfilesActivated() {
        ConfigurableEnvironment environment = new MockEnvironment();
        environment.setDefaultProfiles("dev", "api-docs");

        ActiveProfilesInfoContributor contributor = new ActiveProfilesInfoContributor(environment);

        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        assertThat(info.get("activeProfiles")).asInstanceOf(list(String.class)).contains("dev", "api-docs");
    }
}
