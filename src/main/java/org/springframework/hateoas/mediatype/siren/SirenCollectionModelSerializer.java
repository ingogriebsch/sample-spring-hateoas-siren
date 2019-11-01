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

import static java.util.stream.Collectors.toList;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenCollectionModelSerializer extends AbstractSirenSerializer<CollectionModel<?>> {

    private static final long serialVersionUID = 9054285190464802945L;

    public SirenCollectionModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, null);
    }

    public SirenCollectionModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver, BeanProperty property) {
        super(CollectionModel.class, sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, property);
    }

    @Override
    public void serialize(CollectionModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenEntity sirenEntity = SirenEntity.builder() //
            .classes(newArrayList("collection")) //
            .properties(properties(model)) //
            .entities(entities(model)) //
            .links(linkConverter.to(model.getLinks())) //
            .actions(affordanceModelConverter.convert(model.getLinks())) //
            .title(messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getContent().getClass()))) //
            .build();

        provider.findValueSerializer(SirenEntity.class, property).serialize(sirenEntity, gen, provider);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenCollectionModelSerializer(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver,
            property);
    }

    private Map<String, Object> properties(CollectionModel<?> model) {
        Map<String, Object> content = new HashMap<>();
        content.put("size", model.getContent().size());
        return content;
    }

    private List<Object> entities(CollectionModel<?> model) {
        return model.getContent().stream().map(c -> entity(c)).collect(toList());
    }

    private Object entity(Object embeddable) {
        // if (!EntityModel.class.equals(embeddable.getClass())) {
        // throw new IllegalArgumentException(String.format("Sub-entities must be of type '%s' [but is of type '%s']!",
        // EntityModel.class.getName(), embeddable.getClass().getName()));
        // }

        return embeddable;
    }

}
