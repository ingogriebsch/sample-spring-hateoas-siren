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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenRepresentationModelSerializer extends AbstractSirenSerializer<RepresentationModel<?>> {

    private static final long serialVersionUID = 2893716845519287714L;

    public SirenRepresentationModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, null);
    }

    public SirenRepresentationModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull SirenAffordanceModelConverter affordanceModelConverter,
        @NonNull MessageResolver messageResolver, BeanProperty property) {
        super(RepresentationModel.class, sirenConfiguration, linkConverter, affordanceModelConverter, messageResolver, property);
    }

    @Override
    public void serialize(RepresentationModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenEntity sirenEntity = SirenEntity.builder() //
            .links(linkConverter.to(model.getLinks())) //
            .actions(affordanceModelConverter.convert(model.getLinks())) //
            .title(messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getClass()))) //
            .build();

        JsonSerializer<Object> serializer = provider.findValueSerializer(SirenEntity.class, property);
        serializer.serialize(sirenEntity, gen, provider);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenRepresentationModelSerializer(sirenConfiguration, linkConverter, affordanceModelConverter,
            messageResolver, property);
    }
}
