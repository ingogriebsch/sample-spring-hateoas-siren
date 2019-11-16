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
package com.github.ingogriebsch.sample.spring.hateoas.siren.person;

import static java.util.stream.Collectors.toList;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
class PersonController {

    static final String PATH_FIND_ALL = "/persons";
    static final String PATH_FIND_ONE = "/persons/{id}";
    static final String PATH_INSERT = PATH_FIND_ALL;
    static final String PATH_UPDATE = PATH_FIND_ONE;
    static final String PATH_PATCH = PATH_FIND_ONE;
    static final String PATH_DELETE = PATH_FIND_ONE;

    @NonNull
    private final PersonService personService;

    @GetMapping(path = PATH_FIND_ALL)
    ResponseEntity<CollectionModel<EntityModel<Person>>> findAll() {
        return ok(collectionModel(personService.findAll()));
    }

    @GetMapping(path = PATH_FIND_ONE)
    ResponseEntity<EntityModel<Person>> findOne(@PathVariable Long id) {
        return personService.findOne(id).map(p -> ok(entityModel(p))).orElse(notFound().build());
    }

    @PostMapping(path = PATH_INSERT, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<Person>> insert(@RequestBody @Valid PersonInput personInput) {
        return status(CREATED).body(entityModel(personService.insert(personInput)));
    }

    @PutMapping(path = PATH_UPDATE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<Person>> update(@PathVariable Long id, @RequestBody @Valid PersonInput personInput) {
        return personService.update(id, personInput).map(p -> ok(entityModel(p))).orElse(notFound().build());
    }

    @DeleteMapping(path = PATH_DELETE)
    ResponseEntity<Void> delete(@PathVariable Long id) {
        return personService.delete(id) ? ok().build() : notFound().build();
    }

    private static CollectionModel<EntityModel<Person>> collectionModel(Collection<Person> persons) {
        CollectionModel<EntityModel<Person>> collectionModel = new CollectionModel<>(entityModels(persons));
        collectionModel.add(linkTo(methodOn(PersonController.class).findAll()).withSelfRel()
            .andAffordance(afford(methodOn(PersonController.class).insert(null))));
        return collectionModel;
    }

    private static Collection<EntityModel<Person>> entityModels(Collection<Person> persons) {
        return persons.stream().map(p -> entityModel(p)).collect(toList());
    }

    private static EntityModel<Person> entityModel(Person person) {
        EntityModel<Person> entityModel = new EntityModel<>(person);
        entityModel.add(linkTo(methodOn(PersonController.class).findOne(person.getId())).withSelfRel()
            .andAffordance(afford(methodOn(PersonController.class).update(person.getId(), null)))
            .andAffordance(afford(methodOn(PersonController.class).delete(person.getId()))));
        return entityModel;
    }

}
