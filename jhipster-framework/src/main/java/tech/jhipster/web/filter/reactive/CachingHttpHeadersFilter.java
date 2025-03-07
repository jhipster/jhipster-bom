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

package tech.jhipster.web.filter.reactive;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * This filter is used in production, to put HTTP cache headers with a long expiration time.
 */
public class CachingHttpHeadersFilter implements WebFilter {

    private final long cacheTimeToLive;

    /**
     * <p>Constructor for CachingHttpHeadersFilter.</p>
     *
     * @param cacheTimeToLive a {@link java.lang.Long} object.
     */
    public CachingHttpHeadersFilter(Long cacheTimeToLive) {
        this.cacheTimeToLive = cacheTimeToLive;
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ServerWebExchangeMatchers.pathMatchers("/i18n/**", "/content/**", "/app/**")
            .matches(exchange)
            .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
            .doOnNext(matchResult -> {
                ServerHttpResponse response = exchange.getResponse();
                response.getHeaders().setCacheControl("max-age=" + cacheTimeToLive + ", public");
                response.getHeaders().setPragma("cache");
                response.getHeaders().setExpires(cacheTimeToLive + System.currentTimeMillis());
            })
            .then(Mono.defer(() -> chain.filter(exchange)));
    }
}
