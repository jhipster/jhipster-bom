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

package tech.jhipster.config.locale;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 * Angular cookie saved the locale with a double quote (%22en%22). So the default
 * CookieLocaleResolver#StringUtils.parseLocaleString(localePart)
 * is not able to parse the locale.
 * <p>
 * This class will check if a double quote has been added, if so it will remove it.
 */
public class AngularCookieLocaleResolver extends CookieLocaleResolver {

    private final Logger logger = LoggerFactory.getLogger(AngularCookieLocaleResolver.class);

    /**
     * Constant <code>QUOTE="%22"</code>
     */
    public static final String QUOTE = "%22";

    public AngularCookieLocaleResolver() {
        super();
    }

    public AngularCookieLocaleResolver(String cookieName) {
        super(cookieName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        parseAngularCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        parseAngularCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext() {
            @Override
            public Locale getLocale() {
                return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
            }

            @Override
            public TimeZone getTimeZone() {
                return (TimeZone) request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    private void parseAngularCookieIfNecessary(HttpServletRequest request) {
        LocaleAndTimeZone localeAndTimeZone = null;
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            Cookie cookie = WebUtils.getCookie(request, DEFAULT_COOKIE_NAME);
            if (cookie != null) {
                localeAndTimeZone = parseCookie(cookie);
            }
        }

        if (localeAndTimeZone == null) {
            localeAndTimeZone = new LocaleAndTimeZone(null, null);
        }

        request.setAttribute(
            LOCALE_REQUEST_ATTRIBUTE_NAME,
            localeAndTimeZone.locale != null ? localeAndTimeZone.locale : this.defaultLocaleFunction.apply(request)
        );
        request.setAttribute(
            TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
            localeAndTimeZone.timeZone != null ? localeAndTimeZone.timeZone : this.defaultTimeZoneFunction.apply(request)
        );
    }

    private final Function<HttpServletRequest, Locale> defaultLocaleFunction = request -> {
        Locale defaultLocale = getDefaultLocale();
        return (defaultLocale != null ? defaultLocale : request.getLocale());
    };

    private final Function<HttpServletRequest, TimeZone> defaultTimeZoneFunction = request -> getDefaultTimeZone();

    private LocaleAndTimeZone parseCookie(Cookie cookie) {
        String value = StringUtils.replace(cookie.getValue(), QUOTE, "");
        String localePart = value;
        String timeZonePart = null;
        int spaceIndex = localePart.indexOf(' ');
        if (spaceIndex != -1) {
            localePart = value.substring(0, spaceIndex);
            timeZonePart = value.substring(spaceIndex + 1);
        }
        Locale locale = !"-".equals(localePart) ? StringUtils.parseLocaleString(localePart.replace('-', '_')) : null;
        TimeZone timeZone = timeZonePart != null ? StringUtils.parseTimeZoneString(timeZonePart) : null;

        if (logger.isTraceEnabled()) {
            logger.trace(
                "Parsed cookie value [" +
                cookie.getValue() +
                "] into locale '" +
                locale +
                "'" +
                (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : "")
            );
        }
        return new LocaleAndTimeZone(locale, timeZone);
    }

    private record LocaleAndTimeZone(Locale locale, TimeZone timeZone) {}

    String quote(String string) {
        return QUOTE + string + QUOTE;
    }
}
