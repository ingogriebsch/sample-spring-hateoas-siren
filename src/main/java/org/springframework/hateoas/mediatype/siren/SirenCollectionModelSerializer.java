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
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenCollectionModelSerializer extends AbstractSirenSerializer<CollectionModel<?>> {

    private static final long serialVersionUID = 9054285190464802945L;

    public SirenCollectionModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, messageResolver, null);
    }

    public SirenCollectionModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull MessageResolver messageResolver, BeanProperty property) {
        super(CollectionModel.class, sirenConfiguration, messageResolver, property);
    }

    @Override
    protected SirenEntity convert(CollectionModel<?> model, MessageResolver messageResolver) {
        return SirenEntity.builder().classes(classes(model)).properties(properties(model))
            .entities(entities(model, messageResolver)).links(links(model, messageResolver)).title(title(model, messageResolver))
            .build();
    }

    @Override
    protected JsonSerializer<?> newInstance(SirenConfiguration sirenConfiguration, MessageResolver messageResolver,
        BeanProperty property) {
        return new SirenCollectionModelSerializer(sirenConfiguration, messageResolver, property);
    }

    private static List<String> classes(CollectionModel<?> model) {
        return newArrayList("collection");
    }

    private static String title(CollectionModel<?> model, MessageResolver messageResolver) {
        return messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getContent().getClass()));
    }

    private static Map<String, Object> properties(CollectionModel<?> model) {
        Map<String, Object> content = new HashMap<>();
        content.put("size", model.getContent().size());
        return content;
    }

    private static List<SirenEmbeddable> entities(CollectionModel<?> model, MessageResolver messageResolver) {
        return model.getContent().stream().map(c -> entity(c, messageResolver)).collect(toList());
    }

    private static SirenEmbeddable entity(Object embeddable, MessageResolver messageResolver) {
        if (!EntityModel.class.equals(embeddable.getClass())) {
            throw new IllegalArgumentException(String.format("Sub-entities must be of type '%s' [but is of type '%s']!",
                EntityModel.class.getName(), embeddable.getClass().getName()));
        }

        return entity((EntityModel<?>) embeddable, messageResolver);
    }

    private static SirenEntity entity(EntityModel<?> embeddable, MessageResolver messageResolver) {
        return SirenEntity.builder().classes(classes(embeddable)).rels(newArrayList("item")).properties(properties(embeddable))
            .links(links(embeddable, messageResolver)).build();
    }

    private static Object properties(EntityModel<?> model) {
        return model.getContent();
    }

    private static List<String> classes(EntityModel<?> model) {
        return newArrayList(uncapitalize(model.getContent().getClass().getSimpleName()));
    }

    private static List<SirenLink> links(RepresentationModel<?> model, MessageResolver messageResolver) {
        return model.getLinks().stream().map(l -> link(l, messageResolver)).collect(toList());
    }

    private static SirenLink link(Link link, MessageResolver messageResolver) {
        return SirenLink.builder().rels(newArrayList(link.getRel().value())).href(link.getHref())
            .title(title(link, messageResolver)).build();
    }

    private static String title(Link link, MessageResolver messageResolver) {
        return link.getTitle() != null ? link.getTitle() : messageResolver.resolve(SirenLink.TitleResolvable.of(link.getRel()));
    }
}
