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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.MessageResolver;

public class SirenEntityModelConverterTest {

    @Nested
    class Ctor {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenEntityModelConverter(null, null, null));
        }
    }

    @Nested
    class To {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> defaultConverter().to(null));
        }

        @Test
        public void should_return_output_containing_string_as_property() {
            EntityModel<String> source = new EntityModel<>("test");
            SirenEntity expected = SirenEntity.builder().properties("test").classes(newArrayList("string")).build();
            SirenEntity actual = defaultConverter().to(source);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_containing_integer_as_property() {
            EntityModel<Integer> source = new EntityModel<>(24);
            SirenEntity expected = SirenEntity.builder().properties(24).classes(newArrayList("integer")).build();
            SirenEntity actual = defaultConverter().to(source);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_containing_pojo_as_property() {
            Person pojo = new Person("name", 42);
            EntityModel<Person> source = new EntityModel<>(pojo);
            SirenEntity expected = SirenEntity.builder().properties(pojo).classes(newArrayList("person")).build();
            SirenEntity actual = defaultConverter().to(source);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class From {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> defaultConverter().from(null, null));
        }

        @Test
        public void should_return_output_containing_string_as_content() {
            SirenEntity source = SirenEntity.builder().properties("test").classes(newArrayList("string")).build();
            EntityModel<String> expected = new EntityModel<>("test");
            EntityModel<String> actual = defaultConverter().from(source, String.class);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_containing_integer_as_content() {
            SirenEntity source = SirenEntity.builder().properties(24).classes(newArrayList("integer")).build();
            EntityModel<Integer> expected = new EntityModel<>(24);
            EntityModel<Integer> actual = defaultConverter().from(source, Integer.class);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_containing_pojo_as_content() {
            Person pojo = new Person("name", 42);
            SirenEntity source = SirenEntity.builder().properties(pojo).classes(newArrayList("person")).build();
            EntityModel<Person> expected = new EntityModel<>(pojo);
            EntityModel<Person> actual = defaultConverter().from(source, Person.class);
            assertThat(actual).isEqualTo(expected);
        }
    }

    private static SirenEntityModelConverter defaultConverter() {
        return converter(DEFAULTS_ONLY);
    }

    private static SirenEntityModelConverter converter(MessageResolver messageResolver) {
        return new SirenEntityModelConverter(new SirenLinkConverter(new SirenConfiguration(), messageResolver),
            new SirenAffordanceModelConverter(new SirenConfiguration(), messageResolver), messageResolver);
    }

}
