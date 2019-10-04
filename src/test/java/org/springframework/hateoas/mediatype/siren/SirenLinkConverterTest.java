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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.StaticMessageResolver;

public class SirenLinkConverterTest {

    @Nested
    class Ctor {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenLinkConverter(null));
        }
    }

    @Nested
    class Convert {

        @Test
        public void should_throw_exception_if_link_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenLinkConverter(DEFAULTS_ONLY).convert((Link) null));
        }

        @Test
        public void should_throw_exception_if_iterable_with_links_is_null() {
            assertThrows(IllegalArgumentException.class,
                () -> new SirenLinkConverter(DEFAULTS_ONLY).convert((Iterable<Link>) null));
        }

        @Test
        public void should_throw_exception_if_variable_parameter_list_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenLinkConverter(DEFAULTS_ONLY).convert((Link[]) null));
        }

        @Test
        public void should_return_siren_link_having_same_href_and_rel() {
            Link source = new Link("/persons/1", SELF);
            SirenLink expected = SirenLink.builder().href("/persons/1").rel(SELF.value()).build();

            SirenLink actual = new SirenLinkConverter(DEFAULTS_ONLY).convert(source);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_siren_link_having_title_from_link() {
            Link source = new Link("/persons/1", SELF);
            source = source.withTitle("title");

            SirenLink expected = SirenLink.builder().href("/persons/1").rel(SELF.value()).title("title").build();

            SirenLink actual = new SirenLinkConverter(DEFAULTS_ONLY).convert(source);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_siren_link_having_title_from_message_resolver() {
            Link source = new Link("/persons/1", SELF);

            SirenLink expected = SirenLink.builder().href("/persons/1").rel(SELF.value()).title("title").build();

            SirenLink actual = new SirenLinkConverter(new StaticMessageResolver("title")).convert(source);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_siren_link_having_title_from_link_even_if_available_through_message_resolver() {
            Link source = new Link("/persons/1", SELF);
            source = source.withTitle("title");

            SirenLink expected = SirenLink.builder().href("/persons/1").rel(SELF.value()).title("title").build();

            SirenLink actual = new SirenLinkConverter(new StaticMessageResolver("something")).convert(source);
            assertThat(actual).isEqualTo(expected);
        }
    }
}
