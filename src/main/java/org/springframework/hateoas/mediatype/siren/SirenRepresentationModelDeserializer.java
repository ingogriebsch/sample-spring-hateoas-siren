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

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenRepresentationModelDeserializer extends AbstractSirenDeserializer<RepresentationModel<?>> {

    private static final long serialVersionUID = -3683235541542548855L;

    public SirenRepresentationModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver,
            TypeFactory.defaultInstance().constructType(RepresentationModel.class));
    }

    public SirenRepresentationModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver, @NonNull JavaType contentType) {
        super(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, contentType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType contentType = property == null ? ctxt.getContextualType() : property.getType().getContentType();
        return new SirenRepresentationModelDeserializer(sirenConfiguration, linkConverter, affordanceModelConverter,
            messageResolver, contentType);
    }

    @Override
    public RepresentationModel<?> deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        SirenEntity entity = p.getCodec().readValue(p, SirenEntity.class);
        List<SirenLink> links = entity.getLinks();
        return new RepresentationModel<>(linkConverter.from(links != null ? links : newArrayList()));
    }

}
