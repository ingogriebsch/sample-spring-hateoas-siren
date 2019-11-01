/*-
 * #%L
 * Spring HATEOAS Siren sample
 * %%
 * Copyright (C) 2018 - 2019 Ingo Griebsch
 * %%
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
 * #L%
 */
package org.springframework.hateoas.mediatype.siren;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import org.springframework.hateoas.RepresentationModel;

public abstract class AbstractSirenSerializer<T extends RepresentationModel<?>> extends ContainerSerializer<T>
    implements ContextualSerializer {

    private static final long serialVersionUID = -8665900081601124431L;

    protected final SirenConfiguration sirenConfiguration;
    protected final BeanProperty property;

    protected AbstractSirenSerializer(Class<?> type, SirenConfiguration sirenConfiguration, BeanProperty property) {
        super(type, false);
        this.sirenConfiguration = sirenConfiguration;
        this.property = property;
    }

    @Override
    public JavaType getContentType() {
        return null;
    }

    @Override
    public JsonSerializer<?> getContentSerializer() {
        return null;
    }

    @Override
    public boolean hasSingleElement(T value) {
        return false;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.findValueSerializer(SirenEntity.class, property).serialize(convert(value, sirenConfiguration), gen, provider);
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return null;
    }

    @Deprecated
    protected SirenEntity convert(T value, SirenConfiguration sirenConfiguration) {
        return null;
    }

}
