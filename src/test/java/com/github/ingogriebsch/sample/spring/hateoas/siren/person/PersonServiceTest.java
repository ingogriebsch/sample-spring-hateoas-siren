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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class PersonServiceTest {

    @Nested
    class FindAll {

        @Test
        void should_return_available_persons() throws Exception {
            PersonService personService = new PersonService();
            List<PersonInput> personInputs =
                newArrayList(personInput(), personInput(), personInput(), personInput(), personInput());
            personInputs.stream().forEach(p -> personService.insert(p));

            Pageable pageable = PageRequest.of(0, 10);
            Page<Person> persons = personService.findAll(pageable);
            assertThat(persons).isNotNull();

            assertThat(persons.getPageable()).isEqualTo(pageable);

            List<Person> content = persons.getContent();
            assertThat(content).extracting("name", "age").containsExactlyInAnyOrderElementsOf(
                personInputs.stream().map(pi -> new Tuple(pi.getName(), pi.getAge())).collect(toList()));
        }
    }

    @Nested
    class Search {

        @Test
        void should_return_matching_persons() throws Exception {
            PersonInput mary = personInput("Mary");
            List<PersonInput> personInputs = newArrayList(personInput("Peter"), personInput("Paul"), mary);

            PersonService personService = new PersonService();
            personInputs.stream().forEach(p -> personService.insert(p));

            List<Person> persons = personService.search(mary.getName().toLowerCase());
            assertThat(persons).extracting("name", "age")
                .containsExactlyElementsOf(newArrayList(new Tuple(mary.getName(), mary.getAge())));
        }
    }

    @Nested
    class FindOne {

        @Test
        void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(IllegalArgumentException.class, () -> new PersonService().findOne(null));
        }

        @Test
        void should_return_matching_person_if_available() throws Exception {
            PersonService personService = new PersonService();
            List<PersonInput> personInputs = newArrayList(personInput(), personInput(), personInput());
            List<Person> persons = personInputs.stream().map(p -> personService.insert(p)).collect(toList());

            Person person = persons.get(nextInt(0, persons.size()));
            Optional<Person> optional = personService.findOne(person.getId());

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isEqualTo(person);
        }

        @Test
        void should_return_empty_optional_if_not_available() throws Exception {
            PersonService personService = new PersonService();
            Optional<Person> optional = personService.findOne(nextLong());
            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }

    }

    @Nested
    class Insert {

        @Test
        void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(IllegalArgumentException.class, () -> new PersonService().insert(null));
        }

        @Test
        void should_return_person_instance_if_input_is_legal() throws Exception {
            PersonService personService = new PersonService();
            PersonInput personInput = personInput();

            Person person = personService.insert(personInput);
            assertThat(person).isEqualToComparingOnlyGivenFields(personInput, "name", "age");
        }

    }

    @Nested
    class Update {

        @Test
        void should_throw_exception_if_called_with_null_id() throws Exception {
            assertThrows(IllegalArgumentException.class, () -> new PersonService().update(null, new PersonInput("name", 23)));
        }

        @Test
        void should_throw_exception_if_called_with_null_input() throws Exception {
            assertThrows(IllegalArgumentException.class, () -> new PersonService().update(1L, null));
        }

        @Test
        void should_return_optional_containing_updated_person_instance_if_person_is_known() throws Exception {
            PersonService personService = new PersonService();
            PersonInput personInput = personInput();
            Person person = personService.insert(personInput);

            PersonInput personinput = new PersonInput(personInput.getName(), personInput.getAge() + 10);
            Optional<Person> optional = personService.update(person.getId(), personinput);

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isEqualToComparingOnlyGivenFields(personinput, "name", "age");
        }

        @Test
        void should_return_empty_optional_if_person_is_not_known() throws Exception {
            PersonService personService = new PersonService();
            PersonInput personInput = personInput();

            Optional<Person> optional = personService.update(nextLong(), personInput);

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }
    }

    @Nested
    class Delete {

        @Test
        void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(IllegalArgumentException.class, () -> new PersonService().delete(null));
        }

        @Test
        void should_return_false_if_person_is_not_known() throws Exception {
            PersonService personService = new PersonService();
            assertThat(personService.delete(nextLong())).isFalse();
        }

        @Test
        void should_return_true_if_person_is_known() throws Exception {
            PersonService personService = new PersonService();
            List<PersonInput> personInputs = newArrayList(personInput(), personInput(), personInput());
            List<Person> persons = personInputs.stream().map(p -> personService.insert(p)).collect(toList());

            Person person = persons.get(nextInt(0, persons.size()));
            assertThat(personService.delete(person.getId())).isTrue();

            Optional<Person> optional = personService.findOne(person.getId());
            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }

    }

    private static PersonInput personInput() {
        return personInput(randomAlphabetic(10));
    }

    private static PersonInput personInput(String name) {
        return new PersonInput(name, nextInt(1, 100));
    }

}
