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
import static org.springframework.hateoas.IanaLinkRelations.ITEM;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenPagedModelConverter {

    @NonNull
    private final SirenEntityModelConverter entityConverter;
    @NonNull
    private final SirenLinkConverter linkConverter;
    @NonNull
    private final SirenAffordanceModelConverter affordanceModelConverter;
    @NonNull
    private final MessageResolver messageResolver;

    public SirenEntity convert(@NonNull PagedModel<?> model) {
        return SirenEntity.builder() //
            .classes(classes(model)) //
            .properties(properties(model)) //
            .entities(entities(model)) //
            .links(linkConverter.to(model.getLinks())) //
            .actions(affordanceModelConverter.convert(model.getLinks())) //
            .title(messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getContent().getClass()))) //
            .build();
    }

    private List<SirenEmbeddable> entities(CollectionModel<?> model) {
        return model.getContent().stream().map(c -> entity(c)).collect(toList());
    }

    private SirenEmbeddable entity(Object embeddable) {
        if (!EntityModel.class.equals(embeddable.getClass())) {
            throw new IllegalArgumentException(String.format("Sub-entities must be of type '%s' [but is of type '%s']!",
                EntityModel.class.getName(), embeddable.getClass().getName()));
        }

        return entityConverter.to((EntityModel<?>) embeddable, newArrayList(ITEM));
    }

    private static List<String> classes(PagedModel<?> model) {
        return newArrayList("page");
    }

    private static PageMetadata properties(PagedModel<?> model) {
        return model.getMetadata();
    }
}
