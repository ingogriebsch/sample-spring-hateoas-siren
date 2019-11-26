/*-
 * Spring HATEOAS Siren sample
 * #%L
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
package com.github.ingogriebsch.sample.spring.hateoas.siren.person;

import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.Links.MergeMode.REPLACE_BY_REL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonModelAssembler implements SimpleRepresentationModelAssembler<Person> {

    @NonNull
    private final PagedResourcesAssembler<Person> pagedResourcesAssembler;

    public PagedModel<EntityModel<Person>> toPagedModel(@NonNull Page<Person> page) {
        PagedModel<EntityModel<Person>> model = pagedResourcesAssembler.toModel(page, this);

        Link selfLink = model.getRequiredLink(SELF);
        selfLink = selfLink.andAffordance(afford(methodOn(PersonController.class).insert(null)));

        Links links = model.getLinks();
        links = links.merge(REPLACE_BY_REL, selfLink);

        model.removeLinks();
        model.add(links);

        return model;
    }

    @Override
    public void addLinks(EntityModel<Person> resource) {
        Class<PersonController> controllerType = PersonController.class;
        Long personId = resource.getContent().getId();

        Link selfLink = linkTo(methodOn(controllerType).findOne(personId)).withSelfRel()
            .andAffordance(afford(methodOn(controllerType).update(personId, null)))
            .andAffordance(afford(methodOn(controllerType).delete(personId)));

        resource.add(selfLink);
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<Person>> resources) {
    }

}
