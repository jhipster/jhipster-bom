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

package tech.jhipster.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDFilterTest {

    private UUIDFilter filter;

    private UUID value = UUID.fromString("dbc36987-d354-4ddf-9b53-38ca19b5a409");

    @BeforeEach
    void setup() {
        filter = new UUIDFilter();
    }

    @Test
    void testConstructor() {
        assertThat(filter.getEquals()).isNull();
        assertThat(filter.getNotEquals()).isNull();
        assertThat(filter.getSpecified()).isNull();
        assertThat(filter.getIn()).isNull();
        assertThat(filter.getNotIn()).isNull();
        assertThat(filter.toString()).isEqualTo("UUIDFilter []");
    }

    @Test
    void testCopy() {
        UUIDFilter copy = filter.copy();
        assertThat(copy).isNotSameAs(filter);
        assertThat(copy.getEquals()).isNull();
        assertThat(copy.getNotEquals()).isNull();
        assertThat(copy.getSpecified()).isNull();
        assertThat(copy.getIn()).isNull();
        assertThat(copy.getNotIn()).isNull();
        assertThat(copy.toString()).isEqualTo("UUIDFilter []");
    }

    @Test
    void testSetEquals() {
        Filter<UUID> chain = filter.setEquals(value);
        assertThat(chain).isEqualTo(filter);
        assertThat(filter.getEquals()).isEqualTo(value);
    }

    @Test
    void testSetNotEquals() {
        Filter<UUID> chain = filter.setNotEquals(value);
        assertThat(chain).isEqualTo(filter);
        assertThat(filter.getNotEquals()).isEqualTo(value);
    }

    @Test
    void testSetSpecified() {
        Filter<UUID> chain = filter.setSpecified(true);
        assertThat(chain).isEqualTo(filter);
        assertThat(filter.getSpecified()).isEqualTo(true);
    }

    @Test
    void testSetIn() {
        List<UUID> list = new LinkedList<>();
        Filter<UUID> chain = filter.setIn(list);
        assertThat(chain).isEqualTo(filter);
        assertThat(filter.getIn()).isEqualTo(list);
    }

    @Test
    void testSetNotIn() {
        List<UUID> list = new LinkedList<>();
        Filter<UUID> chain = filter.setNotIn(list);
        assertThat(chain).isEqualTo(filter);
        assertThat(filter.getNotIn()).isEqualTo(list);
    }

    @Test
    void testEquals() {
        UUIDFilter filter2 = new UUIDFilter();
        assertThat(filter).isEqualTo(filter2);
        filter.setEquals(value);
        assertThat(filter2).isNotEqualTo(filter);
        filter2.setEquals(value);
        assertThat(filter).isEqualTo(filter2);
        filter.setNotEquals(value);
        assertThat(filter2).isNotEqualTo(filter);
        filter2.setNotEquals(value);
        assertThat(filter).isEqualTo(filter2);
        filter.setSpecified(false);
        assertThat(filter2).isNotEqualTo(filter);
        filter2.setSpecified(false);
        assertThat(filter).isEqualTo(filter2);
        filter.setIn(Arrays.asList(value, value));
        assertThat(filter2).isNotEqualTo(filter);
        filter2.setIn(Arrays.asList(value, value));
        assertThat(filter).isEqualTo(filter2);
        filter.setNotIn(Arrays.asList(value, value));
        assertThat(filter2).isNotEqualTo(filter);
        filter2.setNotIn(Arrays.asList(value, value));
        assertThat(filter).isEqualTo(filter2);
        UUIDFilter filter3 = new UUIDFilter();
        filter3.setEquals(value);
        assertThat(filter3).isNotEqualTo(filter);
        assertThat(filter3).isNotEqualTo(filter2);
    }

    @Test
    void testHashCode() {
        UUIDFilter filter2 = new UUIDFilter();
        assertThat(filter.hashCode()).isEqualTo(filter2.hashCode());
        filter.setEquals(value);
        filter2.setEquals(value);
        assertThat(filter.hashCode()).isEqualTo(filter2.hashCode());
        filter.setNotEquals(value);
        filter2.setNotEquals(value);
        assertThat(filter.hashCode()).isEqualTo(filter2.hashCode());
        filter.setSpecified(false);
        filter2.setSpecified(false);
        assertThat(filter.hashCode()).isEqualTo(filter2.hashCode());
        filter.setIn(Arrays.asList(value, value));
        filter2.setIn(Arrays.asList(value, value));
        assertThat(filter.hashCode()).isEqualTo(filter2.hashCode());
        filter.setNotIn(Arrays.asList(value, value));
        filter2.setNotIn(Arrays.asList(value, value));
        assertThat(filter.hashCode()).isEqualTo(filter2.hashCode());
        UUIDFilter filter3 = new UUIDFilter();
        filter3.setEquals(value);
        assertThat(filter3.hashCode()).isNotEqualTo(filter.hashCode());
        assertThat(filter3.hashCode()).isNotEqualTo(filter2.hashCode());
    }

    @Test
    void testToString() {
        filter.setEquals(value);
        filter.setNotEquals(value);
        filter.setSpecified(true);
        filter.setIn(new LinkedList<>());
        filter.setNotIn(new LinkedList<>());
        String str = value.toString();
        assertThat(filter.toString()).isEqualTo("UUIDFilter [equals=" + str + ", notEquals=" + str + ", specified=true, in=[], notIn=[]]");
    }
}
