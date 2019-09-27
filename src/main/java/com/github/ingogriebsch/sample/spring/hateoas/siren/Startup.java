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
package com.github.ingogriebsch.sample.spring.hateoas.siren;

import static com.google.common.collect.Lists.newArrayList;

import com.github.ingogriebsch.sample.spring.hateoas.siren.person.Person;
import com.github.ingogriebsch.sample.spring.hateoas.siren.person.PersonInput;
import com.github.ingogriebsch.sample.spring.hateoas.siren.person.PersonService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Startup implements CommandLineRunner {

    @NonNull
    private final PersonService personService;

    @Override
    public void run(String... args) throws Exception {
        newArrayList(new PersonInput("Ingo", 44), new PersonInput("Marcel", 33), new PersonInput("Sophia", 21)).stream()
            .forEach(p -> insert(p));
    }

    private void insert(PersonInput personInput) {
        Person person = personService.insert(personInput);
        log.debug("Inserted person '{}'...", person);
    }
}
