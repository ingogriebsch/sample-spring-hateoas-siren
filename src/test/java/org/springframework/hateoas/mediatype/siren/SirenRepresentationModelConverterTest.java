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
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.StaticMessageResolver;

public class SirenRepresentationModelConverterTest {

    @Nested
    class Ctor {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenRepresentationModelConverter(null, null, null));
        }
    }

    @Nested
    class Convert {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class,
                () -> new SirenRepresentationModelConverter(new SirenLinkConverter(DEFAULTS_ONLY),
                    new SirenAffordanceModelConverter(DEFAULTS_ONLY), DEFAULTS_ONLY).convert(null));
        }

        @Test
        public void should_return_siren_entity_containing_given_link() {
            SirenRepresentationModelConverter converter = new SirenRepresentationModelConverter(
                new SirenLinkConverter(DEFAULTS_ONLY), new SirenAffordanceModelConverter(DEFAULTS_ONLY), DEFAULTS_ONLY);

            RepresentationModel<?> source = new RepresentationModel<>(new Link("/persons/1", SELF));
            SirenEntity expected =
                SirenEntity.builder().link(SirenLink.builder().href("/persons/1").rel(SELF.value()).build()).build();

            SirenEntity actual = converter.convert(source);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void should_return_siren_entity_having_matching_title() {
            StaticMessageResolver messageResolver = new StaticMessageResolver("title");
            SirenRepresentationModelConverter converter = new SirenRepresentationModelConverter(
                new SirenLinkConverter(DEFAULTS_ONLY), new SirenAffordanceModelConverter(DEFAULTS_ONLY), messageResolver);

            RepresentationModel<?> source = new RepresentationModel<>(new Link("/persons/1", SELF));
            SirenEntity expected = SirenEntity.builder().link(SirenLink.builder().href("/persons/1").rel(SELF.value()).build())
                .title("title").build();

            SirenEntity actual = converter.convert(source);
            assertThat(actual).isEqualTo(expected);
        }
    }
}
