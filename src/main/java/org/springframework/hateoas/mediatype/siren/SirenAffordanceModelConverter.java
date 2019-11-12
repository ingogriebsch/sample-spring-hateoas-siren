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
import static org.springframework.hateoas.mediatype.siren.MediaTypes.SIREN_JSON;
import static org.springframework.hateoas.mediatype.siren.SirenConfiguration.RenderTemplatedLinks.AS_ACTION;
import static org.springframework.http.HttpMethod.GET;

import java.util.List;

import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.siren.SirenAction.Field;
import org.springframework.hateoas.mediatype.siren.SirenAction.Field.Type;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenAffordanceModelConverter {

    @NonNull
    private final SirenConfiguration sirenConfiguration;
    @NonNull
    private final MessageResolver messageResolver;

    public List<SirenAction> convert(@NonNull Links links) {
        List<SirenAction> result = newArrayList();
        for (Link link : links) {
            for (SirenAffordanceModel model : affordanceModels(link)) {
                if (!GET.equals(model.getHttpMethod())) {
                    result.add(convert(model));
                }
            }

            if (link.isTemplated() ? sirenConfiguration.shouldRenderTemplatedLinksAs(AS_ACTION) : false) {
                result.add(convert(link));
            }
        }
        return result;
    }

    private SirenAction convert(Link link) {
        return SirenAction.builder() //
            .name(link.getName()) //
            .href(link.getHref()) //
            .title(actionTitle(link.getName())) //
            .fields(fields(link.getTemplate().getVariables())) //
            .build();
    }

    private SirenAction convert(SirenAffordanceModel model) {
        List<Field> fields = fields(model);

        return SirenAction.builder().name(model.getName()).method(model.getHttpMethod()).href(model.getLink().getHref())
            .title(actionTitle(model.getName())).type(actionType(fields, model)).fields(fields).build();
    }

    private List<Field> fields(List<TemplateVariable> variables) {
        return variables.stream().map(v -> field(v)).collect(toList());
    }

    private List<Field> fields(SirenAffordanceModel model) {
        InputPayloadMetadata input = model.getInput();
        if (input == null) {
            return newArrayList();
        }
        return input.stream().map(pm -> field(pm)).collect(toList());
    }

    private Field field(PropertyMetadata propertyMetadata) {
        return Field.builder().name(propertyMetadata.getName()).type(fieldType(propertyMetadata.getType()))
            .title(fieldTitle(propertyMetadata.getName())).build();
    }

    private Field field(TemplateVariable templateVariable) {
        return Field.builder().name(templateVariable.getName()).title(fieldTitle(templateVariable.getName())).build();
    }

    private String actionType(List<Field> fields, SirenAffordanceModel model) {
        return null;
    }

    private Type fieldType(ResolvableType type) {
        return Number.class.isAssignableFrom(type.getRawClass()) ? Type.number : Type.text;
    }

    private String actionTitle(String name) {
        return name != null ? messageResolver.resolve(SirenAction.TitleResolvable.of(name)) : null;
    }

    private String fieldTitle(String name) {
        return name != null ? messageResolver.resolve(SirenAction.Field.TitleResolvable.of(name)) : null;
    }

    private static List<SirenAffordanceModel> affordanceModels(Link link) {
        return link.getAffordances().stream().map(a -> a.getAffordanceModel(SIREN_JSON)).map(SirenAffordanceModel.class::cast)
            .collect(toList());
    }

}
