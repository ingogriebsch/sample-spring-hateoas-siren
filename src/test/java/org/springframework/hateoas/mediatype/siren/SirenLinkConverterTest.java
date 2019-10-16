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
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.UriTemplate.of;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;
import static org.springframework.hateoas.mediatype.siren.SirenConfiguration.RenderTemplatedLinks.AS_ACTION;
import static org.springframework.hateoas.mediatype.siren.SirenConfiguration.RenderTemplatedLinks.AS_LINK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.StaticMessageResolver;

public class SirenLinkConverterTest {

    @Nested
    class Ctor {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenLinkConverter(null, null));
        }
    }

    @Nested
    class To {

        @Test
        public void should_throw_exception_if_input_is_null() {
            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            assertThrows(IllegalArgumentException.class, () -> converter.to((Iterable<Link>) null));
        }

        @Test
        public void should_return_output_having_same_href_and_rel() {
            Link source = new Link("/persons/1", SELF);
            SirenLink expected = SirenLink.builder().href(source.getHref()).rel(source.getRel().value()).build();

            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            List<SirenLink> converted = converter.to(newArrayList(source));
            assertThat(converted).hasSize(1);

            SirenLink actual = converted.iterator().next();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_having_title_from_input() {
            Link source = new Link("/persons/1", SELF).withTitle("title");
            SirenLink expected =
                SirenLink.builder().href(source.getHref()).rel(source.getRel().value()).title(source.getTitle()).build();

            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            List<SirenLink> converted = converter.to(newArrayList(source));
            assertThat(converted).hasSize(1);

            SirenLink actual = converted.iterator().next();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_having_title_from_message_resolver() {
            Link source = new Link("/persons/1", SELF);
            SirenLink expected = SirenLink.builder().href(source.getHref()).rel(source.getRel().value()).title("title").build();

            SirenLinkConverter converter =
                new SirenLinkConverter(new SirenConfiguration(), StaticMessageResolver.of(expected.getTitle()));
            List<SirenLink> converted = converter.to(newArrayList(source));
            assertThat(converted).hasSize(1);

            SirenLink actual = converted.iterator().next();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_having_title_from_input_even_if_available_through_message_resolver() {
            Link source = new Link("/persons/1", SELF).withTitle("title");

            SirenLink expected =
                SirenLink.builder().href(source.getHref()).rel(source.getRel().value()).title(source.getTitle()).build();

            SirenLinkConverter converter =
                new SirenLinkConverter(new SirenConfiguration(), StaticMessageResolver.of("something"));
            List<SirenLink> converted = converter.to(newArrayList(source));
            assertThat(converted).hasSize(1);

            SirenLink actual = converted.iterator().next();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_empty_list_if_input_is_templated_and_configuration_exclude() {
            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(AS_ACTION), DEFAULTS_ONLY);
            List<SirenLink> converted = converter.to(newArrayList(new Link(of("/persons/{id}"), SELF)));
            assertThat(converted).isEmpty();
        }

        @Test
        public void should_return_matching_output_if_input_is_templated_and_configuration_include() {
            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(AS_LINK), DEFAULTS_ONLY);

            Link source = new Link(of("/persons/{id}"), SELF);
            List<SirenLink> converted = converter.to(newArrayList(source));
            assertThat(converted).hasSize(1);

            SirenLink target = converted.iterator().next();
            assertThat(target.getHref()).isEqualTo(source.getHref());
            assertThat(target.getRels()).containsExactly(source.getRel().value());
        }
    }

    @Nested
    class From {

        @Test
        public void should_throw_exception_if_input_is_null() {
            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            assertThrows(IllegalArgumentException.class, () -> converter.from((Iterable<SirenLink>) null));
        }

        @Test
        public void should_return_output_having_same_href_and_rel() {
            Link expected = new Link("/persons/1", SELF);
            SirenLink source = SirenLink.builder().href(expected.getHref()).rel(expected.getRel().value()).build();

            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            List<Link> converted = converter.from(newArrayList(source));
            assertThat(converted).hasSize(1);

            Link actual = converted.iterator().next();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_having_same_title() {
            Link expected = new Link("/persons/1", SELF).withTitle("title");
            SirenLink source =
                SirenLink.builder().href(expected.getHref()).rel(expected.getRel().value()).title(expected.getTitle()).build();

            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            List<Link> converted = converter.from(newArrayList(source));
            assertThat(converted).hasSize(1);

            Link actual = converted.iterator().next();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_having_same_type() {
            Link expected = new Link("/persons/1", SELF).withTitle("title").withType(APPLICATION_JSON_VALUE);
            SirenLink source = SirenLink.builder().href(expected.getHref()).rel(expected.getRel().value())
                .title(expected.getTitle()).type(expected.getType()).build();

            SirenLinkConverter converter = new SirenLinkConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            List<Link> converted = converter.from(newArrayList(source));
            assertThat(converted).hasSize(1);

            Link actual = converted.iterator().next();
            assertThat(actual).isEqualTo(expected);
        }
    }
}
