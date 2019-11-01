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
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenEntityModelSerializer extends AbstractSirenSerializer<EntityModel<?>> {

    private static final long serialVersionUID = 2893716845519287714L;

    private final SirenConfiguration sirenConfiguration;
    private final SirenLinkConverter linkConverter;
    private final SirenAffordanceModelConverter affordanceModelConverter;
    private final MessageResolver messageResolver;

    public SirenEntityModelSerializer(@NonNull SirenConfiguration sirenConfiguration, @NonNull SirenLinkConverter linkConverter,
        @NonNull SirenAffordanceModelConverter affordanceModelConverter, @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, null);
    }

    public SirenEntityModelSerializer(@NonNull SirenConfiguration sirenConfiguration, @NonNull SirenLinkConverter linkConverter,
        @NonNull SirenAffordanceModelConverter affordanceModelConverter, @NonNull MessageResolver messageResolver,
        BeanProperty property) {
        super(EntityModel.class, sirenConfiguration, property);
        this.sirenConfiguration = sirenConfiguration;
        this.linkConverter = linkConverter;
        this.affordanceModelConverter = affordanceModelConverter;
        this.messageResolver = messageResolver;
    }

    @Override
    public void serialize(EntityModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenEntity sirenEntity = SirenEntity.builder() //
            .classes(newArrayList(uncapitalize(model.getContent().getClass().getSimpleName()))) //
            .properties(model.getContent()) //
            .links(linkConverter.to(model.getLinks())) //
            .actions(affordanceModelConverter.convert(model.getLinks())) //
            .title(messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getContent().getClass()))) //
            .build();

        JsonSerializer<Object> serializer = provider.findValueSerializer(SirenEntity.class, property);
        serializer.serialize(sirenEntity, gen, provider);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenEntityModelSerializer(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver,
            property);
    }

}
