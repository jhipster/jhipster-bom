/*
 * Copyright 2016-2025 the original author or authors from the JHipster project.
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

package tech.jhipster.security.ssl;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Base SSL configuration for cipher suite ordering.
 */
@ConditionalOnProperty({"server.ssl.ciphers", "server.ssl.key-store"})
public abstract class BaseSSLConfiguration {

    protected final Logger log;

    protected BaseSSLConfiguration(Logger log) {
        this.log = log;
    }

    protected void configureCipherSuiteOrder() {
        log.info("Configuring SSL settings");
        log.info("Setting user cipher suite order to true");
        applyCipherSuiteConfiguration();
    }

    protected abstract void applyCipherSuiteConfiguration();
}
