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

package tech.jhipster.domain.util;

import java.time.*;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

/**
 * <p>JSR310DateConverters class.</p>
 */
public final class JSR310DateConverters {

    private JSR310DateConverters() {}

    public static class LocalDateToDateConverter implements Converter<LocalDate, Date> {

        public static final LocalDateToDateConverter INSTANCE = new LocalDateToDateConverter();

        private LocalDateToDateConverter() {}

        @Override
        public Date convert(LocalDate source) {
            return source == null ? null : Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    public static class DateToLocalDateConverter implements Converter<Date, LocalDate> {

        public static final DateToLocalDateConverter INSTANCE = new DateToLocalDateConverter();

        private DateToLocalDateConverter() {}

        @Override
        public LocalDate convert(Date source) {
            return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault()).toLocalDate();
        }
    }

    public static class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {

        public static final ZonedDateTimeToDateConverter INSTANCE = new ZonedDateTimeToDateConverter();

        private ZonedDateTimeToDateConverter() {}

        @Override
        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    public static class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {

        public static final DateToZonedDateTimeConverter INSTANCE = new DateToZonedDateTimeConverter();

        private DateToZonedDateTimeConverter() {}

        @Override
        public ZonedDateTime convert(Date source) {
            return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }

    public static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

        public static final LocalDateTimeToDateConverter INSTANCE = new LocalDateTimeToDateConverter();

        private LocalDateTimeToDateConverter() {}

        @Override
        public Date convert(LocalDateTime source) {
            return source == null ? null : Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    public static class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

        public static final DateToLocalDateTimeConverter INSTANCE = new DateToLocalDateTimeConverter();

        private DateToLocalDateTimeConverter() {}

        @Override
        public LocalDateTime convert(Date source) {
            return source == null ? null : LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }

    public static class DurationToLongConverter implements Converter<Duration, Long> {

        public static final DurationToLongConverter INSTANCE = new DurationToLongConverter();

        private DurationToLongConverter() {}

        @Override
        public Long convert(Duration source) {
            return source == null ? null : source.toNanos();
        }
    }

    public static class LongToDurationConverter implements Converter<Long, Duration> {

        public static final LongToDurationConverter INSTANCE = new LongToDurationConverter();

        private LongToDurationConverter() {}

        @Override
        public Duration convert(Long source) {
            return source == null ? null : Duration.ofNanos(source);
        }
    }
}
