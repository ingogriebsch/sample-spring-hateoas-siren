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

import static java.util.Arrays.asList;

import static com.google.common.collect.Iterables.getLast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;
import static org.springframework.hateoas.mediatype.StaticMessageResolver.of;
import static org.springframework.hateoas.mediatype.siren.SirenEntity.TitleResolvable.of;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

public class SirenRepresentationModelConverterTest {

    @Nested
    class Ctor {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenRepresentationModelConverter(null, null, null));
        }
    }

    @Nested
    class To {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> defaultConverter().to(null));
        }

        @Test
        public void should_return_output_containing_given_link() {
            Link sourceLink = new Link("/persons/1", SELF);
            RepresentationModel<?> source = new RepresentationModel<>(sourceLink);
            SirenLink expectedLink = SirenLink.builder().href(sourceLink.getHref()).rel(sourceLink.getRel().value()).build();
            SirenEntity expected = SirenEntity.builder().link(expectedLink).build();

            SirenRepresentationModelConverter converter = defaultConverter();
            SirenEntity actual = converter.to(source);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_output_having_matching_title() {
            Link sourceLink = new Link("/persons/1", SELF);
            RepresentationModel<?> source = new RepresentationModel<>(sourceLink);
            SirenLink expectedLink = SirenLink.builder().href(sourceLink.getHref()).rel(sourceLink.getRel().value()).build();
            SirenEntity expected = SirenEntity.builder().link(expectedLink).title("title").build();

            SirenRepresentationModelConverter converter =
                converter(of(getLast(asList(of(Person.class).getCodes())), expected.getTitle()));
            SirenEntity actual = converter.to(source);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class From {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> defaultConverter().from(null));
        }

        @Test
        public void should_return_output_containing_given_link() {
            Link expectedLink = new Link("/persons/1", SELF);
            RepresentationModel<?> expected = new RepresentationModel<>(expectedLink);
            SirenLink sourceLink = SirenLink.builder().href(expectedLink.getHref()).rel(expectedLink.getRel().value()).build();
            SirenEntity source = SirenEntity.builder().link(sourceLink).build();

            SirenRepresentationModelConverter converter = defaultConverter();
            RepresentationModel<?> actual = converter.from(source);
            assertThat(actual).isEqualTo(expected);
        }
    }

    private static SirenRepresentationModelConverter defaultConverter() {
        return converter(DEFAULTS_ONLY);
    }

    private static SirenRepresentationModelConverter converter(MessageResolver messageResolver) {
        return new SirenRepresentationModelConverter(new SirenLinkConverter(new SirenConfiguration(), messageResolver),
            new SirenAffordanceModelConverter(new SirenConfiguration(), messageResolver), messageResolver);
    }
}
