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

package tech.jhipster.web.filter;

import tech.jhipster.config.JHipsterProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class CachingHttpHeadersFilterTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;
    private JHipsterProperties properties;
    private CachingHttpHeadersFilter filter;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = spy(new MockHttpServletResponse());
        chain = spy(new MockFilterChain());
        properties = new JHipsterProperties();
        filter = new CachingHttpHeadersFilter(properties);
    }

    @AfterEach
    void teardown() {
        filter.destroy();
    }

    @Test
    void testWithoutInit() {
        long secsToLive = CachingHttpHeadersFilter.DEFAULT_SECONDS_TO_LIVE;

        long before = System.currentTimeMillis();
        before -= before % 1000L;

        Throwable caught = catchThrowable(() -> {
            filter.doFilter(request, response, chain);
            verify(chain).doFilter(request, response);
        });

        long after = System.currentTimeMillis();
        after += 1000L - (after % 1000L);

        verify(response).setHeader("Cache-Control", "max-age=" + secsToLive + ", public");
        verify(response).setHeader("Pragma", "cache");
        verify(response).setDateHeader(eq("Expires"), anyLong());
        assertThat(response.getDateHeader("Expires")).isBetween(before + TimeUnit.SECONDS.toMillis(secsToLive), after + TimeUnit.SECONDS.toMillis(secsToLive));
        assertThat(caught).isNull();
    }

    @Test
    void testWithInit() {
        int daysToLive = CachingHttpHeadersFilter.DEFAULT_DAYS_TO_LIVE >>> 2;
        long secsToLive = TimeUnit.DAYS.toSeconds(daysToLive);
        properties.getHttp().getCache().setTimeToLiveInDays(daysToLive);

        long before = System.currentTimeMillis();
        before -= before % 1000L;

        Throwable caught = catchThrowable(() -> {
            filter.init(null);
            filter.doFilter(request, response, chain);
            verify(chain).doFilter(request, response);
        });

        long after = System.currentTimeMillis();
        after += 1000L - (after % 1000L);

        verify(response).setHeader("Cache-Control", "max-age=" + secsToLive + ", public");
        verify(response).setHeader("Pragma", "cache");
        verify(response).setDateHeader(eq("Expires"), anyLong());
        assertThat(response.getDateHeader("Expires")).isBetween(before + TimeUnit.SECONDS.toMillis(secsToLive), after + TimeUnit.SECONDS.toMillis(secsToLive));
        assertThat(caught).isNull();
    }
}
