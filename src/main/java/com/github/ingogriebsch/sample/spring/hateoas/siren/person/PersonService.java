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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.MoreCollectors.toOptional;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lombok.NonNull;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
class PersonService {

    private final List<Person> persons = newArrayList();

    Page<Person> findAll(@NonNull Pageable pageable) {
        return toPage(persons, pageable);
    }

    List<Person> search(@NonNull String name) {
        return persons.stream().filter(p -> name.equalsIgnoreCase(p.getName())).collect(toList());
    }

    Optional<Person> findOne(@NonNull Long id) {
        return persons.stream().filter(p -> p.getId().equals(id)).limit(1).findAny();
    }

    Person insert(@NonNull PersonInput personInput) {
        Person person = new Person(nextId(persons), personInput.getName(), personInput.getAge());
        persons.add(person);
        return person;
    }

    Optional<Person> update(@NonNull Long id, @NonNull PersonInput personInput) {
        Optional<Person> person = persons.stream().filter(p -> id.equals(p.getId())).collect(toOptional());
        person.ifPresent(p -> {
            p.setName(personInput.getName());
            p.setAge(personInput.getAge());
        });
        return person;
    }

    boolean delete(@NonNull Long id) {
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

    static Page<Person> toPage(List<Person> persons, Pageable pageable) {
        List<Person> content = extract(persons, pageable);
        content = sort(content, pageable.getSortOr(by(ASC, "id")));
        return new PageImpl<>(content, pageable, persons.size());
    }

    private static List<Person> extract(List<Person> source, Pageable pageable) {
        int count = source.size();
        int pageSize = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        return source.subList(start, (start + pageSize) > count ? count : (start + pageSize));
    }

    private static List<Person> sort(List<Person> source, Sort sort) {
        Comparator<Person> comparator = null;
        for (Order order : sort) {
            comparator = comparator(order, comparator);
        }

        if (comparator == null) {
            return source;
        }

        source.sort(comparator);
        return source;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Comparator<Person> comparator(Order order, Comparator<Person> comparator) {
        Function<Person, Long> id = Person::getId;
        Function<Person, String> name = Person::getName;
        Function<Person, Integer> age = Person::getAge;

        Function function = null;
        String property = order.getProperty();
        if (property.equals("id")) {
            function = id;
        } else if (property.equals("name")) {
            function = name;
        } else if (property.equals("age")) {
            function = age;
        }

        if (function == null) {
            return null;
        }

        Comparator<Person> result;
        if (comparator == null) {
            result = Comparator.comparing(function);
        } else {
            result = comparator.thenComparing(function);
        }
        return order.isAscending() ? result : new ReverseComparator(result);
    }
}
