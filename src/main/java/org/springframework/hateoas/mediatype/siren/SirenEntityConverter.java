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

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenEntityConverter {

    private final MessageResolver messageResolver;

    public SirenEntityConverter(@NonNull MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    public SirenEntity from(@NonNull RepresentationModel<?> model) {
        return SirenEntity.builder().links(links(model)).build();
    }

    public SirenEntity from(@NonNull EntityModel<?> model) {
        return SirenEntity.builder().classes(classes(model)).properties(properties(model)).links(links(model)).build();
    }

    public SirenEntity from(@NonNull CollectionModel<?> model) {
        return SirenEntity.builder().classes(classes(model)).properties(properties(model)).entities(entities(model))
            .links(links(model)).build();
    }

    public SirenEntity from(@NonNull PagedModel<?> model) {
        return SirenEntity.builder().classes(classes(model)).properties(properties(model)).entities(entities(model)).build();
    }

    private static List<String> classes(PagedModel<?> model) {
        return newArrayList("page");
    }

    private static List<String> classes(CollectionModel<?> model) {
        return newArrayList("collection");
    }

    private static List<String> classes(EntityModel<?> model) {
        return newArrayList(uncapitalize(model.getContent().getClass().getSimpleName()));
    }

    private static Object properties(PagedModel<?> model) {
        return model.getMetadata();
    }

    private static Map<String, Object> properties(CollectionModel<?> model) {
        Map<String, Object> content = new HashMap<>();
        content.put("size", model.getContent().size());
        return content;
    }

    private static Object properties(EntityModel<?> model) {
        return model.getContent();
    }

    private static List<SirenEmbeddable> entities(CollectionModel<?> model) {
        return model.getContent().stream().map(c -> entity(c)).collect(toList());
    }

    private static SirenEmbeddable entity(Object embedded) {
        // FIXME value must be either of type RepresentationModel or of type EntityModel. 'Simple' pojo's are not allowed. Can
        // be a mixed list containing both types of model.
        EntityModel<?> value = (EntityModel<?>) embedded;
        return SirenEntity.builder().classes(classes(value)).rels(newArrayList("item")).properties(properties(value))
            .links(links(value)).build();
    }

    private static List<SirenLink> links(RepresentationModel<?> model) {
        return model.getLinks().stream().map(l -> link(l)).collect(toList());
    }

    private static SirenLink link(Link link) {
        return SirenLink.builder().rels(newArrayList(link.getRel().value())).href(link.getHref()).build();
    }
}
