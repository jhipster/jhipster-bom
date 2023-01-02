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

import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;

/**
 * Interface used in the (@link ConditionBuilder) to help build literal 'value expression' of the Conditions.
 * Converters registered in the (@link DataBaseConfiguration) of the Springboot app
 * will be used for the datatype/data conversion 
 *
 */
public interface ColumnConverterReactive {
	
	/**
     * Converts the value to the target class with the help of the {@link ConversionService}.
     * @param value to convert.
     * @param target class.
     * @param <T> the parameter for the intended type.
     * @return the value which can be constructed from the input.
     */
	public <T> T convert(@Nullable Object value, @Nullable Class<T> target);

}
