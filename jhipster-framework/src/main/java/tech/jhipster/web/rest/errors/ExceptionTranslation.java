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

package tech.jhipster.web.rest.errors;

import org.springframework.web.server.ServerWebExchange;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * The implementation of this interface is generally used to handle exceptions
 * occuring prior to the ExceptionHandler selection in spring-web lifecycle.
 * see https://github.com/spring-projects/spring-framework/issues/22991
 */
public interface ExceptionTranslation {
    
    /**
     * Method to translate an Exception to ProblemDetail.
     * @param ex The exception that needs to be handled
     * @param request The request that is being served
     * @return Returns the Responseentity containing the ProblemDetail
     */
    public Mono<ResponseEntity<Object>> handleAnyException(Throwable ex, ServerWebExchange request);
}
