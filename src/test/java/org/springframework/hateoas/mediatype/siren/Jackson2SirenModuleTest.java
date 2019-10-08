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
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.hateoas.IanaLinkRelations.ABOUT;
import static org.springframework.hateoas.IanaLinkRelations.HELP;
import static org.springframework.hateoas.IanaLinkRelations.LICENSE;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;

import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.SimpleObjectProvider;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;

public class Jackson2SirenModuleTest {

    private static final String REPRESENTATION_MODEL_WITHOUT_LINKS = "{}";
    private static final String REPRESENTATION_MODEL_CONTAINING_LINK = "{\"links\":[{\"rel\":[\"about\"],\"href\":\"/about\"}]}";
    private static final String REPRESENTATION_MODEL_CONTAINING_LINKS =
        "{\"links\":[{\"rel\":[\"about\"],\"href\":\"/about\"},{\"rel\":[\"help\"],\"href\":\"/help\"},{\"rel\":[\"license\"],\"href\":\"/license\"}]}";
    private static final String ENTITY_MODEL_CONTAINING_POJO =
        "{\"class\":[\"person\"],\"properties\":{\"name\":\"Peter\",\"age\":42}}";
    private static final String ENTITY_MODEL_CONTAINING_POJO_AND_SELF_LINK =
        "{\"class\":[\"person\"],\"properties\":{\"name\":\"Peter\",\"age\":42},\"links\":[{\"rel\":[\"self\"],\"href\":\"/persons/1\"}]}";
    private static final String COLLECTION_MODEL_WITHOUT_CONTENT = "{\"class\":[\"collection\"],\"properties\":{\"size\":0}}";
    private static final String COLLECTION_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO =
        "{\"class\":[\"collection\"],\"properties\":{\"size\":1},\"entities\":[{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties\":{\"name\":\"Peter\",\"age\":42}}]}";
    private static final String COLLECTION_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO_AND_SELF_LINK =
        "{\"class\":[\"collection\"],\"properties\":{\"size\":1},\"entities\":[{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties\":{\"name\":\"Peter\",\"age\":42},\"links\":[{\"rel\":[\"self\"],\"href\":\"/persons/1\"}]}]}";
    private static final String PAGED_MODEL_WITHOUT_CONTENT =
        "{\"class\":[\"page\"],\"properties\":{\"size\":0,\"totalElements\":0,\"totalPages\":0,\"number\":0}}";
    private static final String PAGED_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO =
        "{\"class\":[\"page\"],\"properties\":{\"size\":1,\"totalElements\":1,\"totalPages\":1,\"number\":0},\"entities\":[{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties\":{\"name\":\"Peter\",\"age\":42}}]}";
    private static final String PAGED_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO_AND_SELF_LINK =
        "{\"class\":[\"page\"],\"properties\":{\"size\":1,\"totalElements\":1,\"totalPages\":1,\"number\":0},\"entities\":[{\"class\":[\"person\"],\"rel\":[\"item\"],\"properties\":{\"name\":\"Peter\",\"age\":42},\"links\":[{\"rel\":[\"self\"],\"href\":\"/persons/1\"}]}]}";

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll() {
        LinkRelationProvider linkRelationProvider =
            new DelegatingLinkRelationProvider(new AnnotationLinkRelationProvider(), new DefaultLinkRelationProvider());
        SirenMediaTypeConfiguration sirenMediaTypeConfiguration = new SirenMediaTypeConfiguration(
            new SimpleObjectProvider<>(new SirenConfiguration()), linkRelationProvider, DEFAULTS_ONLY);
        objectMapper = sirenMediaTypeConfiguration.configureObjectMapper(new ObjectMapper());
    }

    @Nested
    class Serialize {

        @Nested
        class Representation {

            @Test
            public void without_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>();
                String expected = REPRESENTATION_MODEL_WITHOUT_LINKS;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(new Link("/about", ABOUT));
                String expected = REPRESENTATION_MODEL_CONTAINING_LINK;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(
                    newArrayList(new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));
                String expected = REPRESENTATION_MODEL_CONTAINING_LINKS;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Entity {

            @Test
            public void containing_pojo() throws Exception {
                EntityModel<Person> source = new EntityModel<>(new Person("Peter", 42));
                String expected = ENTITY_MODEL_CONTAINING_POJO;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo_and_self_link() throws Exception {
                EntityModel<Person> source = new EntityModel<>(new Person("Peter", 42), new Link("/persons/1", SELF));
                String expected = ENTITY_MODEL_CONTAINING_POJO_AND_SELF_LINK;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Collection {

            @Test
            public void without_content() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(newArrayList());
                String expected = COLLECTION_MODEL_WITHOUT_CONTENT;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(newArrayList(new EntityModel<>(new Person("Peter", 42))));
                String expected = COLLECTION_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                CollectionModel<?> source =
                    new CollectionModel<>(newArrayList(new EntityModel<>(new Person("Peter", 42), new Link("/persons/1", SELF))));
                String expected = COLLECTION_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO_AND_SELF_LINK;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Paged {

            @Test
            public void without_content() throws Exception {
                PagedModel<?> source = new PagedModel<>(newArrayList(), new PageMetadata(0, 0, 0));
                String expected = PAGED_MODEL_WITHOUT_CONTENT;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                PagedModel<?> source =
                    new PagedModel<>(newArrayList(new EntityModel<>(new Person("Peter", 42))), new PageMetadata(1, 0, 1));
                String expected = PAGED_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                PagedModel<?> source =
                    new PagedModel<>(newArrayList(new EntityModel<>(new Person("Peter", 42), new Link("/persons/1", SELF))),
                        new PageMetadata(1, 0, 1));
                String expected = PAGED_MODEL_CONTAINING_ENTITY_MODEL_CONTAINING_POJO_AND_SELF_LINK;

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    @Disabled
    class Deserialize {

        @Nested
        class Representation {

            @Test
            public void without_links() throws Exception {
                String source = REPRESENTATION_MODEL_WITHOUT_LINKS;
                RepresentationModel<?> expected = new RepresentationModel<>();

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link() throws Exception {
                String source = REPRESENTATION_MODEL_CONTAINING_LINK;
                RepresentationModel<?> expected = new RepresentationModel<>(new Link("/about", ABOUT));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_links() throws Exception {
                String source = REPRESENTATION_MODEL_CONTAINING_LINKS;
                RepresentationModel<?> expected = new RepresentationModel<>(
                    newArrayList(new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Entity {

            @Test
            public void containing_pojo() throws Exception {
                String source = ENTITY_MODEL_CONTAINING_POJO;
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Person.class);
                EntityModel<Person> expected = new EntityModel<>(new Person("Peter", 42));

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo_and_self_link() throws Exception {
                String source = ENTITY_MODEL_CONTAINING_POJO_AND_SELF_LINK;
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Person.class);
                EntityModel<Person> expected = new EntityModel<>(new Person("Peter", 42), new Link("/persons/1", SELF));

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Collection {

            @Test
            public void without_content() throws Exception {
                fail("Implement me... :)");
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                fail("Implement me... :)");
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                fail("Implement me... :)");
            }
        }

        @Nested
        class Paged {

            @Test
            public void without_content() throws Exception {
                fail("Implement me... :)");
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                fail("Implement me... :)");
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                fail("Implement me... :)");
            }
        }
    }

    private static String write(Object object) throws Exception {
        Writer writer = new StringWriter();
        objectMapper.writeValue(writer, object);
        return writer.toString();
    }

    private static <T> T read(String str, JavaType type) throws Exception {
        return objectMapper.readValue(str, type);
    }

    private static <T> T read(String str, Class<T> type) throws Exception {
        return objectMapper.readValue(str, type);
    }
}
