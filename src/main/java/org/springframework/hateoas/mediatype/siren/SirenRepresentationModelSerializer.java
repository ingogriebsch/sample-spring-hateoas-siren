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

import java.util.List;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenRepresentationModelSerializer extends AbstractSirenSerializer<RepresentationModel<?>> {

    private static final long serialVersionUID = 2893716845519287714L;

    public SirenRepresentationModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, messageResolver, null);
    }

    public SirenRepresentationModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull MessageResolver messageResolver, BeanProperty property) {
        super(RepresentationModel.class, sirenConfiguration, messageResolver, property);
    }

    @Override
    protected SirenEntity convert(RepresentationModel<?> model, MessageResolver messageResolver) {
        return SirenEntity.builder().links(links(model, messageResolver)).title(title(model, messageResolver)).build();
    }

    @Override
    protected JsonSerializer<?> newInstance(SirenConfiguration sirenConfiguration, MessageResolver messageResolver,
        BeanProperty property) {
        return new SirenRepresentationModelSerializer(sirenConfiguration, messageResolver, property);
    }

    private static String title(RepresentationModel<?> model, MessageResolver messageResolver) {
        return messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getClass()));
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
