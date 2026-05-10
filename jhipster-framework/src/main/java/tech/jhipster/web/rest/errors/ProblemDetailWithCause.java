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

package tech.jhipster.web.rest.errors;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.ProblemDetail;

/*
 * Class that extends Spring's ProblemDetail and has a Builder implementation.
 */
public class ProblemDetailWithCause extends ProblemDetail {

    private ProblemDetailWithCause cause;

    ProblemDetailWithCause(int rawStatus) {
        super(rawStatus);
    }

    ProblemDetailWithCause(int rawStatus, ProblemDetailWithCause cause) {
        super(rawStatus);
        this.cause = cause;
    }

    public ProblemDetailWithCause getCause() {
        return cause;
    }

    public void setCause(ProblemDetailWithCause cause) {
        this.cause = cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProblemDetailWithCause that) || !super.equals(o)) {
            return false;
        }
        return Objects.equals(cause, that.cause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cause);
    }

    // The missing builder from Spring
    public static class ProblemDetailWithCauseBuilder {

        private static final URI BLANK_TYPE = URI.create("about:blank");
        // From Springs Problem Detail
        private URI type = BLANK_TYPE;
        private String title;
        private int status;
        private String detail;
        private URI instance;
        private Map<String, Object> properties = new HashMap<>();
        private ProblemDetailWithCause cause;

        public static ProblemDetailWithCauseBuilder instance() {
            return new ProblemDetailWithCauseBuilder();
        }

        public ProblemDetailWithCauseBuilder withType(URI type) {
            this.type = type;
            return this;
        }

        public ProblemDetailWithCauseBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ProblemDetailWithCauseBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public ProblemDetailWithCauseBuilder withDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public ProblemDetailWithCauseBuilder withInstance(URI instance) {
            this.instance = instance;
            return this;
        }

        public ProblemDetailWithCauseBuilder withCause(ProblemDetailWithCause cause) {
            this.cause = cause;
            return this;
        }

        public ProblemDetailWithCauseBuilder withProperties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public ProblemDetailWithCauseBuilder withProperty(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }

        public ProblemDetailWithCause build() {
            ProblemDetailWithCause problemDetail = new ProblemDetailWithCause(this.status);
            problemDetail.setType(this.type);
            problemDetail.setTitle(this.title);
            problemDetail.setDetail(this.detail);
            problemDetail.setInstance(this.instance);
            this.properties.forEach(problemDetail::setProperty);
            problemDetail.setCause(this.cause);
            return problemDetail;
        }
    }
}
