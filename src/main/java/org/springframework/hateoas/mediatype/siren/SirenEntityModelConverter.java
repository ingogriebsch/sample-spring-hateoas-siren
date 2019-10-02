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
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenEntityModelConverter {

    @NonNull
    private final SirenLinkConverter sirenLinkConverter;
    @NonNull
    private final MessageResolver messageResolver;

    public SirenEntity convert(@NonNull EntityModel<?> model) {
        return convert(model, null);
    }

    public SirenEntity convert(@NonNull EntityModel<?> model, List<String> rels) {
        return SirenEntity.builder().classes(classes(model)).properties(properties(model))
            .links(sirenLinkConverter.convert(model.getLinks())).rels(rels)
            .title(messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getContent().getClass()))).build();
    }

    private static List<String> classes(EntityModel<?> model) {
        return newArrayList(uncapitalize(model.getContent().getClass().getSimpleName()));
    }

    private static Object properties(EntityModel<?> model) {
        return model.getContent();
    }
}
