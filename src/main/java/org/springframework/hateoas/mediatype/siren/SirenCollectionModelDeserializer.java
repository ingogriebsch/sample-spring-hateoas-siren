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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.util.CollectionUtils;

import lombok.NonNull;

public class SirenCollectionModelDeserializer extends AbstractSirenDeserializer<CollectionModel<?>> {

    private static final long serialVersionUID = 4364222303241126575L;
    private static final JavaType TYPE = defaultInstance().constructType(CollectionModel.class);

    public SirenCollectionModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, TYPE);
    }

    public SirenCollectionModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver, JavaType contentType) {
        super(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, contentType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        return new SirenCollectionModelDeserializer(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver,
            property == null ? ctxt.getContextualType() : property.getType().getContentType());
    }

    @Override
    public CollectionModel<?> deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        List<Object> content = null;
        List<Link> links = null;

        while (jp.nextToken() != null) {
            if (JsonToken.FIELD_NAME.equals(jp.currentToken())) {
                if ("entities".equals(jp.getText())) {
                    content = deserializeContent(jp, ctxt);
                }

                if ("links".equals(jp.getText())) {
                    links = deserializeLinks(jp, ctxt);
                }
            }
        }

        return new CollectionModel<>(content != null ? content : newArrayList(), links != null ? links : newArrayList());
    }

    private List<Object> deserializeContent(JsonParser jp, DeserializationContext ctxt) throws IOException {
        List<JavaType> bindings = contentType.getBindings().getTypeParameters();
        if (CollectionUtils.isEmpty(bindings)) {
            // FIXME
        }

        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(bindings.iterator().next());
        if (deserializer == null) {
            // FIXME
        }

        List<Object> content = newArrayList();
        if (JsonToken.START_ARRAY.equals(jp.nextToken())) {
            while (!JsonToken.END_ARRAY.equals(jp.nextToken())) {
                content.add(deserializer.deserialize(jp, ctxt));
            }
        }
        return content;
    }

    private List<Link> deserializeLinks(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonDeserializer<Object> deserializer =
            ctxt.findContextualValueDeserializer(defaultInstance().constructType(SirenLink.class), null);
        if (deserializer == null) {
            // FIXME
        }

        List<SirenLink> links = newArrayList();
        if (JsonToken.START_ARRAY.equals(jp.nextToken())) {
            while (!JsonToken.END_ARRAY.equals(jp.nextToken())) {
                links.add((SirenLink) deserializer.deserialize(jp, ctxt));
            }
        }
        return linkConverter.from(links);
    }
}