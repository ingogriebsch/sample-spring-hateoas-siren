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
package com.github.ingogriebsch.sample.spring.hateoas.siren.mediatype;

import static com.github.ingogriebsch.sample.spring.hateoas.siren.mediatype.MediaTypes.SIREN_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;

public class SirenMediaTypeConfigurationTest {

    private static SirenMediaTypeConfiguration configuration;

    @BeforeAll
    public static void beforeAll() {
        LinkRelationProvider linkRelationProvider =
            new DelegatingLinkRelationProvider(new AnnotationLinkRelationProvider(), new DefaultLinkRelationProvider());
        configuration =
            new SirenMediaTypeConfiguration(linkRelationProvider, new SimpleObjectProvider<>(new SirenConfiguration()));
    }

    @Nested
    class GetMediaTypes {

        @Test
        public void should_return_matching_media_type() {
            assertThat(configuration.getMediaTypes()).containsExactly(SIREN_JSON);
        }
    }

    @Nested
    class GetJacksonModule {

        @Test
        public void should_return_matching_jackson_module() {
            assertThat(configuration.getJacksonModule()).isNotNull().isInstanceOf(Jackson2SirenModule.class);
        }
    }

    @Nested
    class ConfigureObjectMapper {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(NullPointerException.class, () -> configuration.configureObjectMapper(null));
        }
    }

}
