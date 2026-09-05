/*
 * Copyright 2016-2026 the original author or authors from the JHipster project.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition.Comparator;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

class CriteriaBuilderTest {

    private CriteriaBuilder builder;

    @BeforeEach
    void setup() {
        builder = new CriteriaBuilder();
    }

    @Test
    void shouldBuildEmptyCriteriaWhenNoFilterIsSet() {
        builder.buildFilterCriteriaForField(new StringFilter(), "name");
        builder.buildFilterCriteriaForField(new LongFilter(), "id");

        assertThat(builder.buildCriteria().isEmpty()).isTrue();
    }

    @Test
    void shouldBuildEqualsCriteria() {
        StringFilter filter = new StringFilter();
        filter.setEquals("jhipster");

        builder.buildFilterCriteriaForField(filter, "name");

        Criteria criteria = builder.buildCriteria();
        assertThat(criteria.getColumn()).hasToString("name");
        assertThat(criteria.getComparator()).isEqualTo(Comparator.EQ);
        assertThat(criteria.getValue()).isEqualTo("jhipster");
    }

    @Test
    void shouldBuildGeneralCriteria() {
        BooleanFilter filter = new BooleanFilter();
        filter.setNotEquals(true);
        filter.setIn(List.of(true, false));
        filter.setNotIn(List.of(false));
        filter.setSpecified(true);

        builder.buildFilterCriteriaForField(filter, "active");

        assertThat(builder.buildCriteria()).hasToString(
            "(active != 'true' AND active IN ('true', 'false') AND active NOT IN ('false') AND active IS NOT NULL)"
        );
    }

    @Test
    void shouldBuildNotSpecifiedCriteria() {
        LongFilter filter = new LongFilter();
        filter.setSpecified(false);

        builder.buildFilterCriteriaForField(filter, "id");

        assertThat(builder.buildCriteria()).hasToString("id IS NULL");
    }

    @Test
    void shouldIgnoreEmptyInAndNotIn() {
        LongFilter filter = new LongFilter();
        filter.setIn(List.of());
        filter.setNotIn(List.of());

        builder.buildFilterCriteriaForField(filter, "id");

        assertThat(builder.buildCriteria().isEmpty()).isTrue();
    }

    @Test
    void shouldBuildStringCriteria() {
        StringFilter filter = new StringFilter();
        filter.setContains("jhip");
        filter.setDoesNotContain("spring");

        builder.buildFilterCriteriaForField(filter, "name");

        assertThat(builder.buildCriteria()).hasToString("(name LIKE '%jhip%' AND name NOT LIKE '%spring%')");
    }

    @Test
    void shouldBuildRangeCriteria() {
        LongFilter filter = new LongFilter();
        filter.setGreaterThan(1L);
        filter.setLessThan(10L);
        filter.setGreaterThanOrEqual(2L);
        filter.setLessThanOrEqual(9L);

        builder.buildFilterCriteriaForField(filter, "id");

        assertThat(builder.buildCriteria()).hasToString("(id > 1 AND id < 10 AND id >= 2 AND id <= 9)");
    }

    @Test
    void shouldCombineCriteriaFromMultipleFields() {
        StringFilter nameFilter = new StringFilter();
        nameFilter.setEquals("jhipster");
        LongFilter idFilter = new LongFilter();
        idFilter.setGreaterThan(1L);

        builder.buildFilterCriteriaForField(nameFilter, "name");
        builder.buildFilterCriteriaForField(idFilter, "id");

        assertThat(builder.buildCriteria()).hasToString("(name = 'jhipster' AND id > 1)");
    }
}
