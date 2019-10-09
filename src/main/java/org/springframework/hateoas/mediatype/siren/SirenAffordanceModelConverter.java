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

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

import static org.springframework.hateoas.mediatype.siren.MediaTypes.SIREN_JSON;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.siren.SirenAction.Field;
import org.springframework.hateoas.mediatype.siren.SirenAction.Field.Type;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenAffordanceModelConverter {

    @NonNull
    private final MessageResolver messageResolver;

    public List<SirenAction> convert(@NonNull Links links) {
        return links.stream().flatMap(it -> it.getAffordances().stream()).map(it -> it.getAffordanceModel(SIREN_JSON))
            .map(SirenAffordanceModel.class::cast).map(m -> convert(m)).collect(toList());
    }

    private SirenAction convert(SirenAffordanceModel model) {
        List<Field> fields = fields(model);

        return SirenAction.builder().name(model.getName()).method(model.getHttpMethod()).href(model.getLink().getHref())
            .title(title(model)).type(type(fields, model)).fields(fields).build();
    }

    private String type(List<Field> fields, SirenAffordanceModel model) {
        return null;
    }

    private List<Field> fields(SirenAffordanceModel model) {
        return of(ofNullable(model.getInput())).filter(Optional::isPresent).map(Optional::get).flatMap(i -> i.stream())
            .map(pm -> field(pm)).collect(toList());
    }

    private Field field(PropertyMetadata propertyMetadata) {
        return Field.builder().name(propertyMetadata.getName()).type(type(propertyMetadata.getType()))
            .title(title(propertyMetadata)).build();
    }

    private Type type(ResolvableType type) {
        return Number.class.isAssignableFrom(type.getRawClass()) ? Type.number : Type.text;
    }

    private String title(PropertyMetadata propertyMetadata) {
        return messageResolver.resolve(SirenAction.Field.TitleResolvable.of(propertyMetadata.getName()));
    }

    private String title(SirenAffordanceModel model) {
        return messageResolver.resolve(SirenAction.TitleResolvable.of(model.getName()));
    }
}
