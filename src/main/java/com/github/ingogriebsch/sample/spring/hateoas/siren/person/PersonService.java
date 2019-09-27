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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.MoreCollectors.toOptional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class PersonService {

    private final List<Person> persons = newArrayList();

    public Collection<Person> findAll() {
        return newArrayList(persons);
    }

    public Optional<Person> findOne(@NonNull Long id) {
        return persons.stream().filter(p -> p.getId().equals(id)).limit(1).findAny();
    }

    public Person insert(@NonNull PersonInput personInput) {
        Person person = new Person(nextId(persons), personInput.getName(), personInput.getAge());
        persons.add(person);
        return person;
    }

    public Optional<Person> update(@NonNull Long id, @NonNull PersonInput personInput) {
        Optional<Person> person = persons.stream().filter(p -> id.equals(p.getId())).collect(toOptional());
        person.ifPresent(p -> {
            p.setName(personInput.getName());
            p.setAge(personInput.getAge());
        });
        return person;
    }

    public boolean delete(@NonNull Long id) {
        return persons.removeIf(p -> p.getId().equals(id));
    }

    private static Long nextId(Collection<Person> persons) {
        Long id = 0L;
        for (Person person : persons) {
            if (id < person.getId()) {
                id = person.getId();
            }
        }
        return id + 1L;
    }

}
