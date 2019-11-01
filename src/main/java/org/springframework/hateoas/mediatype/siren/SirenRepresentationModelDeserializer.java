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

import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

public class SirenRepresentationModelDeserializer extends ContainerDeserializerBase<RepresentationModel<?>>
    implements ContextualDeserializer {

    private static final long serialVersionUID = -3683235541542548855L;

    private final SirenRepresentationModelConverter converter;
    private final JavaType contentType;

    public SirenRepresentationModelDeserializer(@NonNull SirenRepresentationModelConverter converter) {
        this(converter, TypeFactory.defaultInstance().constructType(RepresentationModel.class));
    }

    public SirenRepresentationModelDeserializer(@NonNull SirenRepresentationModelConverter converter,
        @NonNull JavaType contentType) {
        super(contentType);
        this.converter = converter;
        this.contentType = contentType;
    }

    @Override
    public RepresentationModel<?> deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        return converter.from(p.getCodec().readValue(p, SirenEntity.class));
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType contentType = property == null ? ctxt.getContextualType() : property.getType().getContentType();
        return new SirenRepresentationModelDeserializer(converter, contentType);
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
