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

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenEntityModelConverter {

    @NonNull
    private final SirenLinkConverter linkConverter;
    @NonNull
    private final SirenAffordanceModelConverter affordanceModelConverter;
    @NonNull
    private final MessageResolver messageResolver;

    public SirenEntity convert(@NonNull EntityModel<?> model) {
        return convert(model, new LinkRelationSupplier() {

            @Override
            public List<LinkRelation> getRels() {
                return newArrayList();
            }
        });
    }

    public SirenEntity convert(@NonNull EntityModel<?> model, @NonNull List<LinkRelation> rels) {
        return convert(model, new LinkRelationSupplier() {

            @Override
            public List<LinkRelation> getRels() {
                return rels;
            }
        });
    }

    public SirenEntity convert(@NonNull EntityModel<?> model, @NonNull LinkRelationSupplier linkRelationSupplier) {
        return SirenEntity.builder().classes(classes(model)) //
            .rels(rels(model, linkRelationSupplier)) //
            .properties(properties(model)) //
            .links(linkConverter.convert(model.getLinks())) //
            .actions(affordanceModelConverter.convert(model.getLinks())) //
            .title(messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getContent().getClass()))) //
            .build();
    }

    private List<LinkRelation> rels(@NonNull EntityModel<?> model, LinkRelationSupplier linkRelationSupplier) {
        return linkRelationSupplier.getRels();
    }

    private static List<String> classes(EntityModel<?> model) {
        return newArrayList(uncapitalize(model.getContent().getClass().getSimpleName()));
    }

    private static Object properties(EntityModel<?> model) {
        return model.getContent();
    }

    @FunctionalInterface
    public static interface LinkRelationSupplier {

        List<LinkRelation> getRels();
    }
}
