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
import static org.springframework.hateoas.Links.of;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;
import static org.springframework.hateoas.mediatype.siren.SirenConfiguration.RenderTemplatedLinks.AS_ACTION;
import static org.springframework.hateoas.mediatype.siren.SirenConfiguration.RenderTemplatedLinks.AS_LINK;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.http.HttpMethod;

public class SirenAffordanceModelConverterTest {

    @Nested
    class Ctor {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenAffordanceModelConverter(null, null));
        }
    }

    @Nested
    class Convert {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class,
                () -> new SirenAffordanceModelConverter(new SirenConfiguration(), DEFAULTS_ONLY).convert(null));
        }

        @Test
        public void should_return_empty_result_if_link_neither_templated_nor_having_affordances() {
            Links links = of(new Link("/persons/1", SELF));

            SirenAffordanceModelConverter converter = new SirenAffordanceModelConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            List<SirenAction> converted = converter.convert(links);

            assertThat(converted).isEmpty();
        }

        @Test
        public void should_return_empty_result_if_link_is_templated_and_has_no_affordances_and_configuration_exclude() {
            Links links = Links.of(new Link(UriTemplate.of("/persons/{id}"), SELF));

            SirenAffordanceModelConverter converter =
                new SirenAffordanceModelConverter(new SirenConfiguration(AS_LINK), DEFAULTS_ONLY);
            List<SirenAction> converted = converter.convert(links);

            assertThat(converted).isEmpty();
        }

        @Test
        public void should_return_siren_link_if_link_if_not_templated_but_has_affordances() {
            Link link = new Link("/persons/1", SELF);
            link = Affordances.of(link).afford(HttpMethod.DELETE).withName("delete").toLink();

            SirenAffordanceModelConverter converter = new SirenAffordanceModelConverter(new SirenConfiguration(), DEFAULTS_ONLY);
            List<SirenAction> converted = converter.convert(Links.of(link));

            assertThat(converted).hasSize(1);
        }

        @Test
        public void should_return_siren_link_if_link_is_templated_and_has_no_affordances_and_configuration_include() {
            Link link = new Link(UriTemplate.of("/persons/{id}"), SELF).withName("person");
            Links links = Links.of(link);

            SirenAffordanceModelConverter converter =
                new SirenAffordanceModelConverter(new SirenConfiguration(AS_ACTION), DEFAULTS_ONLY);
            List<SirenAction> converted = converter.convert(links);

            assertThat(converted).hasSize(1);
        }
    }
}
