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

import java.util.List;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenRepresentationModelConverter {

    @NonNull
    private final SirenLinkConverter linkConverter;
    @NonNull
    private final SirenAffordanceModelConverter affordanceModelConverter;
    @NonNull
    private final MessageResolver messageResolver;

    public SirenEntity convert(@NonNull RepresentationModel<?> model) {
        return SirenEntity.builder().links(links(model)).actions(actions(model)).title(title(model)).build();
    }

    private List<SirenLink> links(RepresentationModel<?> model) {
        return linkConverter.convert(model.getLinks());
    }

    private List<SirenAction> actions(RepresentationModel<?> model) {
        return affordanceModelConverter.convert(model.getLinks());
    }

    private String title(RepresentationModel<?> model) {
        return messageResolver.resolve(SirenEntity.TitleResolvable.of(model.getClass()));
    }
}
