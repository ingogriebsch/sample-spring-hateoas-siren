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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.JacksonHelper;

import lombok.NonNull;

public class SirenEntityModelDeserializer extends ContainerDeserializerBase<EntityModel<?>> implements ContextualDeserializer {

    private static final long serialVersionUID = -3683235541542548855L;

    private final SirenEntityModelConverter converter;
    private final JavaType contentType;

    public SirenEntityModelDeserializer(@NonNull SirenEntityModelConverter converter) {
        this(converter, TypeFactory.defaultInstance().constructType(EntityModel.class));
    }

    public SirenEntityModelDeserializer(@NonNull SirenEntityModelConverter converter, @NonNull JavaType contentType) {
        super(contentType);
        this.converter = converter;
        this.contentType = contentType;
    }

    @Override
    public EntityModel<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JavaType targetType = JacksonHelper.findRootType(this.contentType);
        return converter.from(p.getCodec().readValue(p, SirenEntity.class), targetType.getRawClass());
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType contentType = property == null ? ctxt.getContextualType() : property.getType().getContentType();
        return new SirenEntityModelDeserializer(converter, contentType);
    }

    @Override
    public JavaType getContentType() {
        return contentType;
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return null;
    }

}
