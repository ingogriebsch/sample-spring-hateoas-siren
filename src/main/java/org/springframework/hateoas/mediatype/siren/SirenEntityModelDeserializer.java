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

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.primitives.Primitives;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.JacksonHelper;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.PropertyUtils;

import lombok.NonNull;

public class SirenEntityModelDeserializer extends AbstractSirenDeserializer<EntityModel<?>> {

    private static final long serialVersionUID = -3683235541542548855L;
    private static final JavaType TYPE = defaultInstance().constructType(EntityModel.class);

    public SirenEntityModelDeserializer(@NonNull SirenConfiguration sirenConfiguration, @NonNull SirenLinkConverter linkConverter,
        @NonNull SirenAffordanceModelConverter affordanceModelConverter, @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, TYPE);
    }

    public SirenEntityModelDeserializer(@NonNull SirenConfiguration sirenConfiguration, @NonNull SirenLinkConverter linkConverter,
        @NonNull SirenAffordanceModelConverter affordanceModelConverter, @NonNull MessageResolver messageResolver,
        @NonNull JavaType contentType) {
        super(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, contentType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType contentType = property == null ? ctxt.getContextualType() : property.getType().getContentType();
        return new SirenEntityModelDeserializer(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver,
            contentType);
    }

    @Override
    public EntityModel<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SirenEntity sirenEntity = p.getCodec().readValue(p, SirenEntity.class);
        JavaType targetType = JacksonHelper.findRootType(this.contentType);
        Object content = content(sirenEntity, targetType.getRawClass());
        List<Link> links = linkConverter.from(sirenEntity.getLinks() != null ? sirenEntity.getLinks() : newArrayList());
        return new EntityModel<>(content, links);
    }

    @SuppressWarnings("unchecked")
    private <T> T content(SirenEntity entity, Class<T> targetType) {
        Object properties = entity.getProperties();
        if (properties == null) {
            // FIXME
        }

        Class<? extends Object> propertiesType = properties.getClass();
        if (String.class.equals(propertiesType) || Primitives.isWrapperType(propertiesType)) {
            return (T) properties;
        }

        if (Map.class.isAssignableFrom(propertiesType)) {
            return PropertyUtils.createObjectFromProperties(targetType, (Map<String, Object>) properties);
        }

        return (T) properties;
    }
}
