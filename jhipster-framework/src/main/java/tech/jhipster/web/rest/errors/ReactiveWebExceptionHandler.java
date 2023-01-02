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

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * ResponseStatusException handler for springwebflux(reactive) application
 * that will facilitate the translation to ProblemDetail
 */
public class ReactiveWebExceptionHandler implements org.springframework.web.server.WebExceptionHandler {
    
    private final ExceptionTranslation translator;
    private final ObjectMapper mapper;
    
    public ReactiveWebExceptionHandler(ExceptionTranslation translator, ObjectMapper mapper) {
        this.translator = translator;
        this.mapper = mapper;
    }
    
    /**
	 * Handle the given exception. A completion signal through the return value
	 * indicates error handling is complete while an error signal indicates the
	 * exception is still not handled.
	 * @param exchange the current exchange
	 * @param throwable the exception to handle
	 * @return {@code Mono<Void>} to indicate when exception handling is complete
	 */
    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable throwable) {
        if (throwable instanceof ResponseStatusException) {
            final Mono<ResponseEntity<Object>> entityMono = translator.handleAnyException(throwable, exchange);
            return entityMono.flatMap(entity -> this.setHttpResponse(entity, exchange, mapper));
        }
        return Mono.error(throwable);
    }

    private Mono<Void> setHttpResponse(final ResponseEntity<Object> entity, final ServerWebExchange exchange,
            final ObjectMapper mapper) {
        final ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(entity.getStatusCode());
        response.getHeaders().addAll(entity.getHeaders());
        try {
            final DataBuffer buffer = response.bufferFactory()
                    .wrap(mapper.writeValueAsBytes(entity.getBody()));
            return response.writeWith(Mono.just(buffer))
                    .doOnError(error -> DataBufferUtils.release(buffer));
        } catch (final JsonProcessingException ex) {
            return Mono.error(ex);
        }
    }
}
