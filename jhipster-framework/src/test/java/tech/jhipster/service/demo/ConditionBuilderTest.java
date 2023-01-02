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

package tech.jhipster.service.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Table;

import tech.jhipster.service.ColumnConverterReactive;
import tech.jhipster.service.ConditionBuilder;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.DurationFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.RangeFilter;
import tech.jhipster.service.filter.ShortFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

public class ConditionBuilderTest {

    ConditionBuilder builder;
    private final Duration TEST_DURATION = Duration.ofHours(6);
    private final ZonedDateTime TEST_ZONED_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L),
            ZoneOffset.UTC);
    private final Instant TEST_INSTANT = Instant.ofEpochMilli(0L);
    private final String TEST_STRING = "HAPPY_FILTER";
    private final Boolean TEST_BOOLEAN_TRUE = true;
    private final Boolean TEST_BOOLEAN_FALSE = false;
    private final Long TEST_LONG = 123L;
    private final BigDecimal TEST_BIG_DECIMAL = new BigDecimal(123);
    private final Double TEST_DOUBLE = Double.valueOf(123d);
    private final Float TEST_FLOAT = Float.valueOf(123f);
    private final Integer TEST_INTEGER = Integer.valueOf(123);
    private final Short TEST_SHORT = Short.valueOf((short) 123);
    private final List<String> rangeFilterSetters = Arrays.asList("GreaterThan", "LessThan", "GreaterThanOrEqual",
            "LessThanOrEqual");
    private final List<String> stringFilterSetters = Arrays.asList("Contains", "DoesNotContain");
    private final List<String> generalFilterSetters = Arrays.asList("Equals", "NotEquals", "In", "NotIn", "Specified");

    @BeforeEach
    void setup() {
        builder = new ConditionBuilder(new ColumnConverterReactive() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T convert(Object source, Class<T> target) {
                if (source instanceof Instant) {
                    return (T) LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC);
                }
                if (source instanceof ZonedDateTime) {
                    return (T) ((ZonedDateTime) source).toLocalDateTime();
                }
                if (source instanceof Duration) {
                    return (T) Long.valueOf((((Duration) source).toMillis()));
                }
                if (source instanceof Boolean) {
                    return (T) ((Boolean) source);
                }
                if (Enum.class.isAssignableFrom(target)) {
                    return (T) Enum.valueOf((Class<Enum>) target, source.toString());
                }
                throw new IllegalStateException("Not supported");
            }
        });
    }

    @Test
    void testUsingDurationTypeSingleFilter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        DurationFilter testDuration = (DurationFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals"), true).buildRangeConditions(new DurationFilter(), TEST_DURATION);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '21600000'");
    }

    @Test
    void testUsingDurationTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DurationFilter testDuration = (DurationFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, true).buildRangeConditions(new DurationFilter(), TEST_DURATION);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '21600000' AND c.time_to_go < '21600000' AND c.time_to_go >= '21600000' "
                + "AND c.time_to_go <= '21600000' AND c.time_to_go = '21600000' AND c.time_to_go != '21600000' A"
                + "ND c.time_to_go IN ('21600000') AND c.time_to_go NOT IN ('21600000') AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingDurationTypeWithUultipleIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DurationFilter testDuration = (DurationFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("Equals", "In", "Specified"), false).buildRangeConditions(new DurationFilter(),
                        TEST_DURATION, TEST_DURATION.plusMillis(10));
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go = '21600000' AND c.time_to_go IN ('21600000', '21600010') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingDurationTypeWithUultipleNotIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DurationFilter testDuration = (DurationFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals", "NotIn"), null).buildRangeConditions(new DurationFilter(), TEST_DURATION,
                        TEST_DURATION.plusMillis(10));
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '21600000' AND c.time_to_go NOT IN ('21600000', '21600010')");
    }

    @Test
    void testUsingZonedDateTimeTypeSingleFilter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ZonedDateTimeFilter testDuration = (ZonedDateTimeFilter) new ConditionTestHelper(Arrays.asList(),
                Arrays.asList(), Arrays.asList("NotEquals"), null).buildRangeConditions(new ZonedDateTimeFilter(),
                        TEST_ZONED_DATE_TIME);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '1970-01-01T00:00'");
    }

    @Test
    void testUsingZonedDateTimeTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ZonedDateTimeFilter testDuration = (ZonedDateTimeFilter) new ConditionTestHelper(rangeFilterSetters,
                Arrays.asList(), generalFilterSetters, true).buildRangeConditions(new ZonedDateTimeFilter(),
                        TEST_ZONED_DATE_TIME);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '1970-01-01T00:00' AND c.time_to_go < '1970-01-01T00:00' "
                + "AND c.time_to_go >= '1970-01-01T00:00' AND c.time_to_go <= '1970-01-01T00:00' "
                + "AND c.time_to_go = '1970-01-01T00:00' AND c.time_to_go != '1970-01-01T00:00' "
                + "AND c.time_to_go IN ('1970-01-01T00:00') AND c.time_to_go NOT IN ('1970-01-01T00:00') "
                + "AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingZonedDateTimeTypeWithUultipleIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ZonedDateTimeFilter testDuration = (ZonedDateTimeFilter) new ConditionTestHelper(Arrays.asList(),
                Arrays.asList(), Arrays.asList("Equals", "In", "Specified"), false).buildRangeConditions(
                        new ZonedDateTimeFilter(), TEST_ZONED_DATE_TIME, TEST_ZONED_DATE_TIME.plusSeconds(1));
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go = '1970-01-01T00:00' AND c.time_to_go IN ('1970-01-01T00:00', '1970-01-01T00:00:01') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingZonedDateTimeTypeWithUultipleNotIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ZonedDateTimeFilter testDuration = (ZonedDateTimeFilter) new ConditionTestHelper(Arrays.asList(),
                Arrays.asList(), Arrays.asList("NotEquals", "NotIn"), null).buildRangeConditions(
                        new ZonedDateTimeFilter(), TEST_ZONED_DATE_TIME, TEST_ZONED_DATE_TIME.plusSeconds(1));
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go != '1970-01-01T00:00' AND c.time_to_go NOT IN ('1970-01-01T00:00', '1970-01-01T00:00:01')");
    }

    @Test
    void testUsingInstantTypeSingleFilter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        InstantFilter testDuration = (InstantFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals"), null).buildRangeConditions(new InstantFilter(), TEST_INSTANT);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '1970-01-01T00:00'");
    }

    @Test
    void testUsingInstantTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        InstantFilter testDuration = (InstantFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, true).buildRangeConditions(new InstantFilter(), TEST_INSTANT);
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go > '1970-01-01T00:00' AND c.time_to_go < '1970-01-01T00:00' AND c.time_to_go >= '1970-01-01T00:00' "
                        + "AND c.time_to_go <= '1970-01-01T00:00' AND c.time_to_go = '1970-01-01T00:00' AND c.time_to_go != '1970-01-01T00:00' "
                        + "AND c.time_to_go IN ('1970-01-01T00:00') AND c.time_to_go NOT IN ('1970-01-01T00:00') AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingInstantTypeWithUultipleIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        InstantFilter testDuration = (InstantFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("Equals", "In", "Specified"), false).buildRangeConditions(new InstantFilter(),
                        TEST_INSTANT, TEST_INSTANT.plusMillis(10));
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go = '1970-01-01T00:00' AND c.time_to_go IN ('1970-01-01T00:00', '1970-01-01T00:00:00.010') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingInstantTypeWithUultipleNotIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        InstantFilter testDuration = (InstantFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals", "NotIn"), null).buildRangeConditions(new InstantFilter(), TEST_INSTANT,
                        TEST_INSTANT.plusMillis(10));
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go != '1970-01-01T00:00' AND c.time_to_go NOT IN ('1970-01-01T00:00', '1970-01-01T00:00:00.010')");
    }

    @Test
    void testUsingStringTypeSingleFilter() throws IllegalAccessException, InvocationTargetException {
        StringFilter testDuration = (StringFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals"), null).buildStringConditions(new StringFilter(), TEST_STRING);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != 'HAPPY_FILTER'");
    }

    @Test
    void testUsingStringTypeWithAllFilters() throws IllegalAccessException, InvocationTargetException {
        StringFilter testDuration = (StringFilter) new ConditionTestHelper(Arrays.asList(), stringFilterSetters,
                generalFilterSetters, true).buildStringConditions(new StringFilter(), TEST_STRING);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go LIKE 'HAPPY_FILTER' AND c.time_to_go NOT LIKE 'HAPPY_FILTER' AND "
                + "c.time_to_go = 'HAPPY_FILTER' AND c.time_to_go != 'HAPPY_FILTER' "
                + "AND c.time_to_go IN ('HAPPY_FILTER') AND c.time_to_go NOT IN ('HAPPY_FILTER') AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingStringTypeWithMultipleIn() throws IllegalAccessException, InvocationTargetException {
        StringFilter testDuration = (StringFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("Equals", "In", "Specified"), false).buildStringConditions(new StringFilter(),
                        TEST_STRING, TEST_STRING + "!!!");
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go = 'HAPPY_FILTER' AND c.time_to_go IN ('HAPPY_FILTER', 'HAPPY_FILTER!!!') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingStringTypeWithMultipleNotIn() throws IllegalAccessException, InvocationTargetException {
        StringFilter testDuration = (StringFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals", "NotIn"), null).buildStringConditions(new StringFilter(), TEST_STRING,
                        TEST_STRING + "!!!");
        setupBuilder(testDuration);
        assertConditionBuild(
                "c.time_to_go != 'HAPPY_FILTER' AND c.time_to_go NOT IN ('HAPPY_FILTER', 'HAPPY_FILTER!!!')");
    }

    @Test
    void testUsingBooleanTypeSingleFilter() throws IllegalAccessException, InvocationTargetException {
        BooleanFilter testDuration = (BooleanFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals"), null).buildGeneralConditions(new BooleanFilter(), TEST_BOOLEAN_TRUE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != true");
    }

    @Test
    void testUsingBooleanTypeWithAllFilters() throws IllegalAccessException, InvocationTargetException {
        BooleanFilter testDuration = (BooleanFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                generalFilterSetters, true).buildGeneralConditions(new BooleanFilter(), TEST_BOOLEAN_TRUE,
                        TEST_BOOLEAN_FALSE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = true AND c.time_to_go != true "
                + "AND c.time_to_go IN (true, false) AND c.time_to_go NOT IN (true, false) AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingBooleanTypeWithMultipleIn() throws IllegalAccessException, InvocationTargetException {
        BooleanFilter testDuration = (BooleanFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("Equals", "In", "Specified"), false).buildGeneralConditions(new BooleanFilter(),
                        TEST_BOOLEAN_TRUE, TEST_BOOLEAN_FALSE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = true AND c.time_to_go IN (true, false) AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingDBooleanTypeWithMultipleNotIn() throws IllegalAccessException, InvocationTargetException {
        BooleanFilter testDuration = (BooleanFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals", "NotIn"), null).buildGeneralConditions(new BooleanFilter(),
                        TEST_BOOLEAN_TRUE, TEST_BOOLEAN_FALSE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != true AND c.time_to_go NOT IN (true, false)");
    }

    @Test
    void testUsingLongTypeSingleFilter() throws IllegalAccessException, InvocationTargetException {
        LongFilter testDuration = (LongFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals"), null).buildGeneralConditions(new LongFilter(), TEST_LONG);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '123'");
    }

    @Test
    void testUsingLongTypeWithAllFilters() throws IllegalAccessException, InvocationTargetException {
        LongFilter testDuration = (LongFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, true).buildRangeConditions(new LongFilter(), TEST_LONG);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123' AND c.time_to_go < '123' AND c.time_to_go >= '123' "
                        + "AND c.time_to_go <= '123' AND c.time_to_go = '123' AND c.time_to_go != '123' "
                        + "AND c.time_to_go IN ('123') AND c.time_to_go NOT IN ('123') AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingLongTypeWithUultipleIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        LongFilter testDuration = (LongFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("Equals", "In", "Specified"), false).buildRangeConditions(new LongFilter(), TEST_LONG,
                        TEST_LONG + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = '123' AND c.time_to_go IN ('123', '124') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingLongTypeWithUultipleNotIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        LongFilter testDuration = (LongFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals", "NotIn"), null).buildRangeConditions(new LongFilter(), TEST_LONG,
                        TEST_LONG + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '123' AND c.time_to_go NOT IN ('123', '124')");
    }

    @Test
    void testUsingBigDecimalTypeSingleFilter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        BigDecimalFilter testDuration = (BigDecimalFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals"), null).buildRangeConditions(new BigDecimalFilter(), TEST_BIG_DECIMAL);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '123'");
    }

    @Test
    void testUsingBigDecimalTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        BigDecimalFilter testDuration = (BigDecimalFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, true).buildRangeConditions(new BigDecimalFilter(), TEST_BIG_DECIMAL);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123' AND c.time_to_go < '123' AND c.time_to_go >= '123' "
                        + "AND c.time_to_go <= '123' AND c.time_to_go = '123' AND c.time_to_go != '123' "
                        + "AND c.time_to_go IN ('123') AND c.time_to_go NOT IN ('123') AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingBigDecimalTypeWithUultipleIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        BigDecimalFilter testDuration = (BigDecimalFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("Equals", "In", "Specified"), false).buildRangeConditions(new BigDecimalFilter(),
                        TEST_BIG_DECIMAL, new BigDecimal(1));
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = '123' AND c.time_to_go IN ('123', '1') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingBigDecimalTypeWithUultipleNotIn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        BigDecimalFilter testDuration = (BigDecimalFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("NotEquals", "NotIn"), null).buildRangeConditions(new BigDecimalFilter(),
                        TEST_BIG_DECIMAL, new BigDecimal(1));
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go != '123' AND c.time_to_go NOT IN ('123', '1')");
    }

    @Test
    void testUsingDoubleTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DoubleFilter testDuration = (DoubleFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, true).buildRangeConditions(new DoubleFilter(), TEST_DOUBLE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123.0' AND c.time_to_go < '123.0' AND c.time_to_go >= '123.0' "
                        + "AND c.time_to_go <= '123.0' AND c.time_to_go = '123.0' AND c.time_to_go != '123.0' "
                        + "AND c.time_to_go IN ('123.0') AND c.time_to_go NOT IN ('123.0') AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingDoubleTypeWithRangeFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DoubleFilter testDuration = (DoubleFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                Arrays.asList(), false).buildRangeConditions(new DoubleFilter(), TEST_DOUBLE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123.0' AND c.time_to_go < '123.0' AND c.time_to_go >= '123.0' "
                        + "AND c.time_to_go <= '123.0'");
    }

    @Test
    void testUsingDoubleTypeOnlySpecified()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DoubleFilter testDuration = (DoubleFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                Arrays.asList("Specified"), false).buildRangeConditions(new DoubleFilter(), TEST_DOUBLE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go IS NULL");
    }

    @Test
    void testUsingDoubleTypeGeneralFilter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DoubleFilter testDuration = (DoubleFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                generalFilterSetters, false).buildRangeConditions(new DoubleFilter(), TEST_DOUBLE);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = '123.0' AND c.time_to_go != '123.0' "
                        + "AND c.time_to_go IN ('123.0') AND c.time_to_go NOT IN ('123.0') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingFloatTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FloatFilter testDuration = (FloatFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, true).buildRangeConditions(new FloatFilter(), TEST_FLOAT, TEST_FLOAT + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123.0' AND c.time_to_go < '123.0' AND c.time_to_go >= '123.0' "
                        + "AND c.time_to_go <= '123.0' AND c.time_to_go = '123.0' AND c.time_to_go != '123.0' "
                        + "AND c.time_to_go IN ('123.0', '124.0') AND c.time_to_go NOT IN ('123.0', '124.0') AND c.time_to_go IS NOT NULL");
    }

    @Test
    void testUsingFloatTypeWithOnlyRange()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FloatFilter testDuration = (FloatFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                Arrays.asList(), null).buildRangeConditions(new FloatFilter(), TEST_FLOAT, TEST_FLOAT + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123.0' AND c.time_to_go < '123.0' AND c.time_to_go >= '123.0' "
                        + "AND c.time_to_go <= '123.0'");
    }

    @Test
    void testUsingFloatTypeWithOnlyGeneral()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FloatFilter testDuration = (FloatFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                generalFilterSetters, false).buildRangeConditions(new FloatFilter(), TEST_FLOAT, TEST_FLOAT + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = '123.0' AND c.time_to_go != '123.0' "
                        + "AND c.time_to_go IN ('123.0', '124.0') AND c.time_to_go NOT IN ('123.0', '124.0') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingIntegerTypeWithOnlyGeneral()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        IntegerFilter testDuration = (IntegerFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                generalFilterSetters, false).buildRangeConditions(new IntegerFilter(), TEST_INTEGER, TEST_INTEGER + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = '123' AND c.time_to_go != '123' "
                        + "AND c.time_to_go IN ('123', '124') AND c.time_to_go NOT IN ('123', '124') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingIntegerTypeWithOnlyRange()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        IntegerFilter testDuration = (IntegerFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                Arrays.asList(), null).buildRangeConditions(new IntegerFilter(), TEST_INTEGER, TEST_INTEGER + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123' AND c.time_to_go < '123' "
                        + "AND c.time_to_go >= '123' AND c.time_to_go <= '123'");
    }

    @Test
    void testUsingIntegerTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        IntegerFilter testDuration = (IntegerFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, false).buildRangeConditions(new IntegerFilter(), TEST_INTEGER, TEST_INTEGER + 1);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123' AND c.time_to_go < '123' "
                        + "AND c.time_to_go >= '123' AND c.time_to_go <= '123' AND c.time_to_go = '123' AND c.time_to_go != '123' "
                        +
                        "AND c.time_to_go IN ('123', '124') AND c.time_to_go NOT IN ('123', '124') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingShortTypeWithAllFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ShortFilter testDuration = (ShortFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                generalFilterSetters, false).buildRangeConditions(new ShortFilter(), TEST_SHORT, TEST_SHORT);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123' AND c.time_to_go < '123' "
                        + "AND c.time_to_go >= '123' AND c.time_to_go <= '123' AND c.time_to_go = '123' AND c.time_to_go != '123' "
                        +
                        "AND c.time_to_go IN ('123', '123') AND c.time_to_go NOT IN ('123', '123') AND c.time_to_go IS NULL");
    }

    @Test
    void testUsingShortTypeWithRangeFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ShortFilter testDuration = (ShortFilter) new ConditionTestHelper(rangeFilterSetters, Arrays.asList(),
                Arrays.asList(), false).buildRangeConditions(new ShortFilter(), TEST_SHORT, TEST_SHORT);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go > '123' AND c.time_to_go < '123' "
                        + "AND c.time_to_go >= '123' AND c.time_to_go <= '123'");
    }

    private enum TestEnum {
        ENUM1, ENUM2;
    };

    class TestEnumFilter extends Filter<TestEnum> {

    }

    @Test
    void testUsingEnumTypeWithGeneralFilters()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        TestEnumFilter testDuration = (TestEnumFilter) new ConditionTestHelper(Arrays.asList(), Arrays.asList(),
                generalFilterSetters, false).buildGeneralConditions(new TestEnumFilter(), TestEnum.ENUM1,
                        TestEnum.ENUM2);
        setupBuilder(testDuration);
        assertConditionBuild("c.time_to_go = 'ENUM1' AND c.time_to_go != 'ENUM1' "
                        + "AND c.time_to_go IN ('ENUM1', 'ENUM2') AND c.time_to_go NOT IN ('ENUM1', 'ENUM2') AND c.time_to_go IS NULL");
    }
    
    private void assertConditionBuild(String expectation) {
        Assertions.assertEquals(builder.buildConditions().toString(), expectation);
    }

    private <X> void setupBuilder(Filter<X> filterInstance) {
        builder.buildFilterConditionForField(filterInstance,
                Column.create("time_to_go", Table.aliased("customer", "c")));
    }
}

class ConditionTestHelper {
    private final List<String> rangeFilterSetters;
    private final List<String> generalFilterSetters;
    private final List<String> stringFilterSetters;
    private final Boolean specified;

    ConditionTestHelper(List<String> rangeFilterSetters, List<String> stringFilterSetters,
            List<String> generalFilterSetters, Boolean specified) {
        this.generalFilterSetters = generalFilterSetters;
        this.rangeFilterSetters = rangeFilterSetters;
        this.stringFilterSetters = stringFilterSetters;
        this.specified = specified;
    }

    public <X extends Comparable<? super X>> RangeFilter<X> buildRangeConditions(RangeFilter<X> filterInstance,
            @SuppressWarnings("unchecked") X... filterValue)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        invokeSetter(filterInstance, generalFilterSetters, specified, filterValue);
        invokeSetter(filterInstance, rangeFilterSetters, null, filterValue);
        return filterInstance;
    }

    public StringFilter buildStringConditions(StringFilter filterInstance, String... filterValue)
            throws IllegalAccessException, InvocationTargetException {
        invokeSetter(filterInstance, stringFilterSetters, specified, filterValue);
        invokeSetter(filterInstance, generalFilterSetters, specified, filterValue);
        return filterInstance;
    }

    public <X> Filter<X> buildGeneralConditions(Filter<X> filterInstance,
            @SuppressWarnings("unchecked") X... filterValue) throws IllegalAccessException, InvocationTargetException {
        invokeSetter(filterInstance, generalFilterSetters, specified, filterValue);
        return filterInstance;
    }

    private <X> void invokeSetter(Filter<X> filterInstance, List<String> fieldsToSet, Boolean specified,
            @SuppressWarnings("unchecked") X... filterValue) throws IllegalAccessException, InvocationTargetException {
        for (String eachMethodName : fieldsToSet) {
            java.lang.reflect.Method method;
            method = getMethod(filterInstance, "set" + eachMethodName);
            if (method.getName().toLowerCase().contains("setin")
                    || method.getName().toLowerCase().contains("setnotin")) {
                method.invoke(filterInstance, Arrays.asList(filterValue));
                continue;
            } else if (method.getName().toLowerCase().contains("specified")) {
                method.invoke(filterInstance, specified);
                continue;
            }
            if (method.getParameters()[0].getType() == Object.class) {
                method.invoke((Object) filterInstance, filterValue[0]);
            } else {
                method.invoke(filterInstance, filterValue[0]);
            }
        }
    }

    private <X> Method getMethod(Filter<X> filterInstance, String eachMethodName) {
        return Arrays.asList(filterInstance.getClass().getMethods()).stream()
                .filter(eachMethod -> eachMethod.getName().equalsIgnoreCase(eachMethodName)).findFirst().get();
    }
}
