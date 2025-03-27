package tech.jhipster.web.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tech.jhipster.config.JHipsterProperties;

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
        long currentTime = System.currentTimeMillis();

        Throwable caught = catchThrowable(() -> {
            filter.doFilter(request, response, chain);
            verify(chain).doFilter(request, response);
        });

        verify(response).setHeader("Cache-Control", "max-age=" + secsToLive + ", public");
        verify(response).setHeader("Pragma", "cache");
        verify(response).setDateHeader(eq("Expires"), anyLong());

        long expires = response.getDateHeader("Expires");
        long expectedExpires = currentTime + TimeUnit.SECONDS.toMillis(secsToLive);

        assertThat(expires).isBetween(
            expectedExpires - 1000,
            expectedExpires + 1000
        );
        assertThat(caught).isNull();
    }

    @Test
    void testWithInit() {
        int daysToLive = CachingHttpHeadersFilter.DEFAULT_DAYS_TO_LIVE >>> 2;
        long secsToLive = TimeUnit.DAYS.toSeconds(daysToLive);
        properties.getHttp().getCache().setTimeToLiveInDays(daysToLive);
        long currentTime = System.currentTimeMillis();

        Throwable caught = catchThrowable(() -> {
            filter.init(null);
            filter.doFilter(request, response, chain);
            verify(chain).doFilter(request, response);
        });

        verify(response).setHeader("Cache-Control", "max-age=" + secsToLive + ", public");
        verify(response).setHeader("Pragma", "cache");
        verify(response).setDateHeader(eq("Expires"), anyLong());

        long expires = response.getDateHeader("Expires");
        long expectedExpires = currentTime + TimeUnit.SECONDS.toMillis(secsToLive);

        assertThat(expires).isBetween(
            expectedExpires - 1000,
            expectedExpires + 1000
        );
        assertThat(caught).isNull();
    }
}
