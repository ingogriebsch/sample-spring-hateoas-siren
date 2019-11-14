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
import static org.springframework.hateoas.IanaLinkRelations.APPENDIX;
import static org.springframework.hateoas.IanaLinkRelations.SELF;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.support.MappingUtils;

public class SirenLinkDiscovererTest {

    private static final SirenLinkDiscoverer discoverer = new SirenLinkDiscoverer();

    @Nested
    class FindLinkWithRel {

        @Nested
        class FromString {

            @Test
            public void should_return_empty_optional_if_not_available() throws IOException {
                String source = MappingUtils.read(resource("representationmodel-containing-links.json"));
                assertThat(discoverer.findLinkWithRel(APPENDIX, source)).isEmpty();
            }

            @Test
            public void should_return_self_link() throws IOException {
                String source = MappingUtils.read(resource("representationmodel-containing-links.json"));

                Optional<Link> link = discoverer.findLinkWithRel(SELF, source);
                assertThat(link).map(Link::getHref).hasValue("/employees");
            }

            @ParameterizedTest
            @CsvSource(value = { "self,/employees", "about,/about", "help,/help", "license,/license" })
            public void should_return_matching_link(LinkRelation rel, String href) throws IOException {
                String source = MappingUtils.read(resource("representationmodel-containing-links.json"));
                Optional<Link> link = discoverer.findLinkWithRel(rel, source);
                assertThat(link).map(Link::getHref).hasValue(href);
            }
        }

        @Nested
        class FromInputStream {

            @Test
            public void should_return_empty_optional_if_not_available() throws IOException {
                try (InputStream source = source("representationmodel-containing-links.json")) {
                    assertThat(discoverer.findLinkWithRel(APPENDIX, source)).isEmpty();
                }
            }

            @Test
            public void should_return_self_link() throws IOException {
                try (InputStream source = source("representationmodel-containing-links.json")) {
                    Optional<Link> link = discoverer.findLinkWithRel(SELF, source);
                    assertThat(link).map(Link::getHref).hasValue("/employees");
                }
            }

            @ParameterizedTest
            @CsvSource(value = { "self,/employees", "about,/about", "help,/help", "license,/license" })
            public void should_return_matching_link(LinkRelation rel, String href) throws IOException {
                try (InputStream source = source("representationmodel-containing-links.json")) {
                    Optional<Link> link = discoverer.findLinkWithRel(rel, source);
                    assertThat(link).map(Link::getHref).hasValue(href);
                }
            }
        }

    }

    @Nested
    class FindLinksWithRel {

        @Nested
        class FromString {

            @Test
            public void should_return_empty_optional_if_not_available() throws IOException {
                String source = MappingUtils.read(resource("collectionmodel-containing-entitymodels.json"));
                assertThat(discoverer.findLinksWithRel(APPENDIX, source)).isEmpty();
            }

            @Test
            public void should_return_self_link() throws IOException {
                String source = MappingUtils.read(resource("collectionmodel-containing-entitymodels.json"));
                assertThat(discoverer.findLinksWithRel(SELF, source)) //
                    .extracting("href") //
                    .containsExactlyInAnyOrder("/employees");
            }

            @Test
            public void should_return_employee_links() throws IOException {
                String source = MappingUtils.read(resource("collectionmodel-containing-entitymodels.json"));
                assertThat(discoverer.findLinksWithRel("employee", source)) //
                    .extracting("href") //
                    .containsExactlyInAnyOrder("/employees/1", "/employees/2", "/employees/3", "/employees/4");
            }
        }

        @Nested
        class FromInputStream {

            @Test
            public void should_return_empty_optional_if_not_available() throws IOException {
                try (InputStream source = source("collectionmodel-containing-entitymodels.json")) {
                    assertThat(discoverer.findLinkWithRel(APPENDIX, source)).isEmpty();
                }
            }

            @Test
            public void should_return_self_link() throws IOException {
                try (InputStream source = source("collectionmodel-containing-entitymodels.json")) {
                    assertThat(discoverer.findLinksWithRel(SELF, source)) //
                        .extracting("href") //
                        .containsExactlyInAnyOrder("/employees");
                }
            }

            @Test
            public void should_return_employee_links() throws IOException {
                try (InputStream source = source("collectionmodel-containing-entitymodels.json")) {
                    assertThat(discoverer.findLinksWithRel("employee", source)) //
                        .extracting("href") //
                        .containsExactlyInAnyOrder("/employees/1", "/employees/2", "/employees/3", "/employees/4");
                }
            }
        }
    }

    private InputStream source(String path) throws IOException {
        return resource(path).getInputStream();
    }

    private Resource resource(String path) {
        return new ClassPathResource(path, getClass());
    }

}
