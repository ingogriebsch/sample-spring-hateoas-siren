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

import static java.util.Optional.empty;
import static java.util.Optional.of;

import static com.github.ingogriebsch.sample.spring.hateoas.siren.person.PersonController.PATH_DELETE;
import static com.github.ingogriebsch.sample.spring.hateoas.siren.person.PersonController.PATH_FIND_ALL;
import static com.github.ingogriebsch.sample.spring.hateoas.siren.person.PersonController.PATH_FIND_ONE;
import static com.github.ingogriebsch.sample.spring.hateoas.siren.person.PersonController.PATH_INSERT;
import static com.github.ingogriebsch.sample.spring.hateoas.siren.person.PersonService.toPage;
import static com.google.common.collect.Lists.newArrayList;
import static de.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ingogriebsch.sample.spring.hateoas.siren.HateoasConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ComponentScan(basePackages = "de.ingogriebsch.spring.hateoas.siren")
@Import(value = { HateoasConfiguration.class, PersonHateoasConfiguration.class })
@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonService personService;

    @Nested
    class FindAll {

        @AfterEach
        void afterEach() {
            reset(personService);
        }

        @Test
        void should_return_ok_including_resources_if_some_available() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            List<Person> persons =
                newArrayList(new Person(1L, "Ingo", 44), new Person(2L, "Edina", 21), new Person(3L, "Marcus", 37));
            given(personService.findAll(pageable)).willReturn(toPage(persons, pageable));

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ALL).params(pageableParams(pageable)).accept(SIREN_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(SIREN_JSON));

            actions.andExpect(jsonPath("$.class", is(not(empty())))) //
                .andExpect(jsonPath("$.properties", is(not(empty())))) //
                .andExpect(jsonPath("$.entities", is(not(empty())))) //
                .andExpect(jsonPath("$.links", is(not(empty())))) //
                .andExpect(jsonPath("$.actions", is(not(empty()))));

            verify(personService, times(1)).findAll(pageable);
            verifyNoMoreInteractions(personService);
        }

        @Test
        void should_return_ok_without_resources_if_none_available() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            given(personService.findAll(pageable)).willReturn(toPage(newArrayList(), pageable));

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ALL).params(pageableParams(pageable)).accept(SIREN_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(SIREN_JSON));

            actions.andExpect(jsonPath("$.class", is(not(empty())))) //
                .andExpect(jsonPath("$.properties", is(not(empty())))) //
                .andExpect(jsonPath("$.links", is(not(empty())))) //
                .andExpect(jsonPath("$.actions", is(not(empty()))));

            verify(personService, times(1)).findAll(pageable);
            verifyNoMoreInteractions(personService);
        }

        private MultiValueMap<String, String> pageableParams(Pageable pageable) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("page", "" + pageable.getPageNumber());
            map.add("size", "" + pageable.getPageSize());
            return map;
        }
    }

    @Nested
    class FindOne {

        @AfterEach
        void afterEach() {
            reset(personService);
        }

        @Test
        void shoud_return_ok_and_person_resource_if_available() throws Exception {
            Person person = new Person(1L, "Kamil", 32);
            given(personService.findOne(person.getId())).willReturn(of(person));

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, person.getId()).accept(SIREN_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(SIREN_JSON));

            actions.andExpect(jsonPath("$.class", is(not(empty())))) //
                .andExpect(jsonPath("$.properties", is(not(empty())))) //
                .andExpect(jsonPath("$.links", is(not(empty())))) //
                .andExpect(jsonPath("$.actions", is(not(empty()))));

            verify(personService, times(1)).findOne(person.getId());
            verifyNoMoreInteractions(personService);
        }

        @Test
        void should_return_not_found_if_not_available() throws Exception {
            Long id = nextLong();
            given(personService.findOne(id)).willReturn(empty());

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, id).accept(SIREN_JSON));
            actions.andExpect(status().isNotFound());

            verify(personService, times(1)).findOne(id);
            verifyNoMoreInteractions(personService);
        }
    }

    @Nested
    class Insert {

        @AfterEach
        void afterEach() {
            reset(personService);
        }

        @Test
        void should_return_created_if_input_is_legal() throws Exception {
            PersonInput personInput = new PersonInput("Kamil", 32);
            given(personService.insert(personInput)).willReturn(new Person(1L, personInput.getName(), personInput.getAge()));

            ResultActions actions = mockMvc.perform(post(PATH_INSERT).accept(SIREN_JSON).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personInput)));

            actions.andExpect(status().isCreated());
            actions.andExpect(content().contentType(SIREN_JSON));

            actions.andExpect(jsonPath("$.class", is(not(empty())))) //
                .andExpect(jsonPath("$.properties", is(not(empty())))) //
                .andExpect(jsonPath("$.links", is(not(empty())))) //
                .andExpect(jsonPath("$.actions", is(not(empty()))));

            verify(personService, times(1)).insert(personInput);
            verifyNoMoreInteractions(personService);
        }

        @Test
        void should_return_bad_request_if_input_is_not_legal() throws Exception {
            ResultActions actions = mockMvc.perform(
                post(PATH_INSERT).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(new PersonInput())));

            actions.andExpect(status().isBadRequest());

            verifyNoInteractions(personService);
        }
    }

    @Nested
    class Update {

        @AfterEach
        void afterEach() {
            reset(personService);
        }

        // FIXME Implement more tests
    }

    @Nested
    class Delete {

        @AfterEach
        void afterEach() {
            reset(personService);
        }

        @Test
        void should_return_ok_if_known() throws Exception {
            Long id = nextLong();
            given(personService.delete(id)).willReturn(true);

            ResultActions actions = mockMvc.perform(delete(PATH_DELETE, id));
            actions.andExpect(status().isOk());

            verify(personService, times(1)).delete(id);
            verifyNoMoreInteractions(personService);
        }

        @Test
        void should_return_not_found_if_not_known() throws Exception {
            Long id = nextLong();
            given(personService.delete(id)).willReturn(false);

            ResultActions actions = mockMvc.perform(delete(PATH_DELETE, id));
            actions.andExpect(status().isNotFound());

            verify(personService, times(1)).delete(id);
            verifyNoMoreInteractions(personService);
        }
    }
}
