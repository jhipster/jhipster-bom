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

package tech.jhipster.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.SQL;

import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DurationFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.RangeFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Class for constructing org.springframework.data.relational.core.sql.Condition
 * from the Entitycriteria i.e. tech.jhipster.service.filter.Filter
 *
 */
public class ConditionBuilder {

    private final List<Condition> allFilters = new ArrayList<Condition>();

    private final ColumnConverterReactive columnConverter;

    public ConditionBuilder(ColumnConverterReactive columnConverter) {
        this.columnConverter = columnConverter;
    }
    
    /**
     * Method that takes in a filter field and a column to construct a compounded SQL Condition.
     * The built condition can be retrieved using the buildConditions function
     * @param <X> 		Field type
     * @param field 	The actual Filter field
     * @param column 	The column for which the condition is constructed
     */
    public <X> void buildFilterConditionForField(Filter<X> field, Column column) {
        if (field instanceof DurationFilter) {
            buildRangeConditions((DurationFilter) field, column, Long.class);
            buildGeneralConditions(field, column, Long.class);
        } else if (field instanceof ZonedDateTimeFilter) {
            buildRangeConditions((ZonedDateTimeFilter) field, column, LocalDateTime.class);
            buildGeneralConditions(field, column, LocalDateTime.class);
        } else if (field instanceof InstantFilter) {
            buildRangeConditions((InstantFilter) field, column, LocalDateTime.class);
            buildGeneralConditions(field, column, LocalDateTime.class);
        } else if (field instanceof RangeFilter) {
            buildRangeConditions((RangeFilter) field, column, null);
            buildGeneralConditions(field, column, null);
        } else if (field instanceof StringFilter) {
            buildStringConditions((StringFilter) field, column);
            buildGeneralConditions(field, column, null);
        } else if (field instanceof BooleanFilter) {
            buildBooleanConditions(field, column);
        } else {
            buildGeneralConditions(field, column, null);
        }
    }

    /**
     * Method that builds and returns the compounded Condition object. This method can be called 
     * multiple times as the Conditions are being built.
     * @return returns the compounded Condition object
     */
    public Condition buildConditions() {
        return allFilters
            .stream()
            .reduce(
                null,
                (Condition cumulated, Condition eachCondition) -> {
                    return cumulated != null ? cumulated.and(eachCondition) : eachCondition;
                }
            );
    }
    
    private <X> Function<X, String> columnValueConverter(Class<?> targetClass) {
        if (targetClass != null) {
            return (value) -> columnConverter.convert(value, targetClass).toString();
        } else {
            return (value) -> value.toString();
        }
    }

    private <X extends Comparable<? super X>> void buildRangeConditions(RangeFilter<X> rangeData, Column column, Class<?> targetClass) {
        var converterFunction = columnValueConverter(targetClass);
        if (rangeData.getGreaterThan() != null) {
            allFilters.add(
                Conditions.isGreater(column, SQL.literalOf(converterFunction.apply(rangeData.getGreaterThan())))
            );
        }
        if (rangeData.getLessThan() != null) {
            allFilters.add(
                Conditions.isLess(column, SQL.literalOf(converterFunction.apply(rangeData.getLessThan())))
            );
        }
        if (rangeData.getGreaterThanOrEqual() != null) {
            allFilters.add(
                Conditions.isGreaterOrEqualTo(
                    column,
                    SQL.literalOf(converterFunction.apply(rangeData.getGreaterThanOrEqual()))
                )
            );
        }
        if (rangeData.getLessThanOrEqual() != null) {
            allFilters.add(
                Conditions.isLessOrEqualTo(
                    column,
                    SQL.literalOf(converterFunction.apply(rangeData.getLessThanOrEqual()))
                )
            );
        }
    }

    private void buildStringConditions(StringFilter stringData, Column column) {
        if (stringData.getContains() != null) {
            allFilters.add(Conditions.like(column, SQL.literalOf(stringData.getContains())));
        }
        if (stringData.getDoesNotContain() != null) {
            allFilters.add(Conditions.notLike(column, SQL.literalOf(stringData.getDoesNotContain())));
        }
    }

    private <X> void buildBooleanConditions(Filter<X> generalData, Column column) {
        if (generalData.getEquals() != null) {
            allFilters.add(Conditions.isEqual(column, SQL.literalOf(columnConverter.convert(generalData.getEquals(), Boolean.class))));
        }
        if (generalData.getNotEquals() != null) {
            allFilters.add(
                Conditions.isNotEqual(column, SQL.literalOf(columnConverter.convert(generalData.getNotEquals(), Boolean.class)))
            );
        }
        if (generalData.getIn() != null && generalData.getIn().size() > 0) {
            allFilters.add(
                Conditions.in(
                    column,
                    generalData
                        .getIn()
                        .stream()
                        .map(eachIn -> SQL.literalOf(columnConverter.convert(eachIn, Boolean.class)))
                        .collect(Collectors.toList())
                )
            );
        }
        if (generalData.getNotIn() != null && generalData.getNotIn().size() > 0) {
            allFilters.add(
                Conditions.notIn(
                    column,
                    generalData
                        .getNotIn()
                        .stream()
                        .map(eachNotIn -> SQL.literalOf(columnConverter.convert(eachNotIn, Boolean.class)))
                        .collect(Collectors.toList())
                )
            );
        }
        if (generalData.getSpecified() != null && generalData.getSpecified()) {
            allFilters.add(Conditions.isNull(column).not());
        }
        if (generalData.getSpecified() != null && !generalData.getSpecified()) {
            allFilters.add(Conditions.isNull(column));
        }
    }

    private <X> void buildGeneralConditions(Filter<X> generalData, Column column, Class<?> targetClass) {
        var converterFunction = columnValueConverter(targetClass);
        if (generalData.getEquals() != null) {
            allFilters.add(
                Conditions.isEqual(column, SQL.literalOf(converterFunction.apply(generalData.getEquals())))
            );
        }
        if (generalData.getNotEquals() != null) {
            allFilters.add(
                Conditions.isNotEqual(column, SQL.literalOf(converterFunction.apply(generalData.getNotEquals())))
            );
        }
        if (generalData.getIn() != null && generalData.getIn().size() > 0) {
            allFilters.add(
                Conditions.in(
                    column,
                    generalData
                        .getIn()
                        .stream()
                        .map(eachIn -> SQL.literalOf(converterFunction.apply(eachIn)))
                        .collect(Collectors.toList())
                )
            );
        }
        if (generalData.getNotIn() != null && generalData.getNotIn().size() > 0) {
            allFilters.add(
                Conditions.notIn(
                    column,
                    generalData
                        .getNotIn()
                        .stream()
                        .map(eachNotIn -> SQL.literalOf(converterFunction.apply(eachNotIn)))
                        .collect(Collectors.toList())
                )
            );
        }
        if (generalData.getSpecified() != null && generalData.getSpecified()) {
            allFilters.add(Conditions.isNull(column).not());
        }
        if (generalData.getSpecified() != null && !generalData.getSpecified()) {
            allFilters.add(Conditions.isNull(column));
        }
    }
}
