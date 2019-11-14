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

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.IanaLinkRelations.ABOUT;
import static org.springframework.hateoas.IanaLinkRelations.HELP;
import static org.springframework.hateoas.IanaLinkRelations.LICENSE;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.UriTemplate.of;
import static org.springframework.hateoas.mediatype.Affordances.of;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
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
import org.springframework.hateoas.support.Employee;
import org.springframework.hateoas.support.MappingUtils;
import org.springframework.web.util.UriComponentsBuilder;

public class Jackson2SirenModuleTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll() {
        LinkRelationProvider linkRelationProvider =
            new DelegatingLinkRelationProvider(new AnnotationLinkRelationProvider(), new DefaultLinkRelationProvider());
        SirenMediaTypeConfiguration sirenMediaTypeConfiguration = new SirenMediaTypeConfiguration(
            new SimpleObjectProvider<>(new SirenConfiguration()), linkRelationProvider, DEFAULTS_ONLY);
        objectMapper = sirenMediaTypeConfiguration.configureObjectMapper(new ObjectMapper());
        objectMapper.configure(INDENT_OUTPUT, true);
    }

    @Nested
    class Serialize {

        @Nested
        class Representation {

            @Test
            public void without_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>();
                String expected = readResource("representationmodel-without-links.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(new Link("/about", ABOUT));
                String expected = readResource("representationmodel-containing-link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link_with_title() throws Exception {
                Link link = new Link("/about", ABOUT).withTitle("about");
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representationmodel-containing-link-with-title.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(newArrayList(new Link("/employees", SELF),
                    new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));
                String expected = readResource("representationmodel-containing-links.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link_with_delete_affordance() throws Exception {
                Link link = of(new Link("/employees/1", SELF)).afford(DELETE).withName("delete").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representationmodel-containing-link-with-delete-affordance.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link_with_update_affordance() throws Exception {
                Link link = of(new Link("/employees/1", SELF)).afford(PUT).withInput(Employee.class).withName("update").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representationmodel-containing-link-with-update-affordance.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link_with_update_and_delete_affordances() throws Exception {
                Link link = of(new Link("/employees/1", SELF)).afford(PUT).withInput(Employee.class).withName("update")
                    .andAfford(DELETE).withName("delete").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representationmodel-containing-link-with-update-and-delete-affordances.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Entity {

            @Test
            public void containing_string() throws Exception {
                EntityModel<String> source = new EntityModel<>("Something");
                String expected = readResource("entitymodel-containing-string.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_integer() throws Exception {
                EntityModel<Integer> source = new EntityModel<>(42);
                String expected = readResource("entitymodel-containing-integer.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo() throws Exception {
                EntityModel<Employee> source = new EntityModel<>(new Employee("Peter", "Carpenter"));
                String expected = readResource("entitymodel-containing-pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo_and_self_link() throws Exception {
                EntityModel<Employee> source =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                String expected = readResource("entitymodel-containing-pojo-and-self-link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

        }

        @Nested
        class Collection {

            @Test
            public void without_content() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(newArrayList());
                String expected = readResource("collectionmodel-without-content.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void with_self_link() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(newArrayList(), new Link("/employees", SELF));
                String expected = readResource("collectionmodel-with-self-link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo() throws Exception {
                CollectionModel<Employee> source = new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")));
                String expected = readResource("collectionmodel-containing-pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo_and_self_link() throws Exception {
                CollectionModel<Employee> source =
                    new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")), new Link("/employees", SELF));
                String expected = readResource("collectionmodel-containing-pojo-and-self-link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                CollectionModel<?> source =
                    new CollectionModel<>(newArrayList(new EntityModel<>(new Employee("Peter", "Carpenter"))));
                String expected = readResource("collectionmodel-containing-entitymodel-containing-pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(
                    newArrayList(new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF))));
                String expected = readResource("collectionmodel-containing-entitymodel-containing-pojo-and-self-link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Paged {

            @Test
            public void without_content() throws Exception {
                PagedModel<?> source = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0));
                String expected = readResource("pagedmodel-without-content.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void with_self_link() throws Exception {
                PagedModel<?> source = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(new Link("/employees", SELF), PageRequest.of(0, 20)));
                String expected = readResource("pagedmodel-with-self-link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo() throws Exception {
                PagedModel<Employee> source =
                    new PagedModel<>(newArrayList(new Employee("Peter", "Carpenter")), new PageMetadata(20, 0, 1));
                String expected = readResource("pagedmodel-containing-pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                EntityModel<Employee> entityModel = new EntityModel<>(new Employee("Peter", "Carpenter"));
                PagedModel<EntityModel<Employee>> source =
                    new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("pagedmodel-containing-entitymodel-containing-pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                EntityModel<Employee> entityModel =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                PagedModel<EntityModel<Employee>> source =
                    new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("pagedmodel-containing-entitymodel-containing-pojo-and-self-link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class Deserialize {

        @Nested
        class Representation {

            @Test
            public void without_links() throws Exception {
                String source = readResource("representationmodel-without-links.json");
                RepresentationModel<?> expected = new RepresentationModel<>();

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_link() throws Exception {
                String source = readResource("representationmodel-containing-link.json");
                RepresentationModel<?> expected = new RepresentationModel<>(new Link("/about", ABOUT));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_links() throws Exception {
                String source = readResource("representationmodel-containing-links.json");
                RepresentationModel<?> expected = new RepresentationModel<>(newArrayList(new Link("/employees", SELF),
                    new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Entity {

            @Test
            public void containing_string() throws Exception {
                String source = readResource("entitymodel-containing-string.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, String.class);
                EntityModel<String> expected = new EntityModel<>("Something");

                EntityModel<String> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_integer() throws Exception {
                String source = readResource("entitymodel-containing-integer.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Integer.class);
                EntityModel<Integer> expected = new EntityModel<>(42);

                EntityModel<Employee> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo() throws Exception {
                String source = readResource("entitymodel-containing-pojo.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Employee.class);
                EntityModel<Employee> expected = new EntityModel<>(new Employee("Peter", "Carpenter"));

                EntityModel<Employee> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo_and_self_link() throws Exception {
                String source = readResource("entitymodel-containing-pojo-and-self-link.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Employee.class);
                EntityModel<Employee> expected =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));

                EntityModel<Employee> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Collection {

            @Test
            public void without_content() throws Exception {
                String source = readResource("collectionmodel-without-content.json");
                CollectionModel<?> expected = new CollectionModel<>(newArrayList());

                CollectionModel<?> actual = read(source, CollectionModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void with_self_link() throws Exception {
                String source = readResource("collectionmodel-with-self-link.json");
                CollectionModel<?> expected = new CollectionModel<>(newArrayList(), new Link("/employees", SELF));

                CollectionModel<?> actual = read(source, CollectionModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo() throws Exception {
                String source = readResource("collectionmodel-containing-pojo.json");
                CollectionModel<Employee> expected = new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")));

                CollectionModel<Employee> actual = read(source, new TypeReference<CollectionModel<Employee>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo_and_self_link() throws Exception {
                String source = readResource("collectionmodel-containing-pojo-and-self-link.json");
                CollectionModel<Employee> expected =
                    new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")), new Link("/employees", SELF));

                CollectionModel<Employee> actual = read(source, new TypeReference<CollectionModel<Employee>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                String source = readResource("collectionmodel-containing-entitymodel-containing-pojo.json");
                EntityModel<Employee> entityModel = new EntityModel<>(new Employee("Peter", "Carpenter"));
                CollectionModel<EntityModel<Employee>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Employee>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Employee>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                String source = readResource("collectionmodel-containing-entitymodel-containing-pojo-and-self-link.json");
                EntityModel<Employee> entityModel =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                CollectionModel<EntityModel<Employee>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Employee>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Employee>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Paged {

            @Test
            public void without_content() throws Exception {
                String source = readResource("pagedmodel-without-content.json");
                PagedModel<?> expected = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0));

                PagedModel<?> actual = read(source, PagedModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void with_self_link() throws Exception {
                String source = readResource("pagedmodel-with-self-link.json");
                PagedModel<?> expected = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(new Link("/employees", SELF), PageRequest.of(0, 20)));

                PagedModel<?> actual = read(source, PagedModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_pojo() throws Exception {
                String source = readResource("pagedmodel-containing-pojo.json");
                PagedModel<Employee> expected =
                    new PagedModel<>(newArrayList(new Employee("Peter", "Carpenter")), new PageMetadata(20, 0, 1));

                PagedModel<Employee> actual = read(source, new TypeReference<PagedModel<Employee>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo() throws Exception {
                String source = readResource("pagedmodel-containing-entitymodel-containing-pojo.json");
                EntityModel<Employee> entityModel = new EntityModel<>(new Employee("Peter", "Carpenter"));
                PagedModel<EntityModel<Employee>> expected =
                    new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));

                PagedModel<EntityModel<Employee>> actual = read(source, new TypeReference<PagedModel<EntityModel<Employee>>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void containing_entity_model_containing_pojo_and_self_link() throws Exception {
                String source = readResource("pagedmodel-containing-entitymodel-containing-pojo-and-self-link.json");
                EntityModel<Employee> entityModel =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                CollectionModel<EntityModel<Employee>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Employee>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Employee>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    private String readResource(String sourceFilename) throws IOException {
        return MappingUtils.read(new ClassPathResource(sourceFilename, getClass()));
    }

    private static <T> T read(String str, TypeReference<T> type) throws Exception {
        return objectMapper.readValue(str, type);
    }

    private static <T> T read(String str, JavaType type) throws Exception {
        return objectMapper.readValue(str, type);
    }

    private static <T> T read(String str, Class<T> type) throws Exception {
        return objectMapper.readValue(str, type);
    }

    private static String write(Object object) throws Exception {
        Writer writer = new StringWriter();
        objectMapper.writeValue(writer, object);
        return writer.toString();
    }

    private static Link enhance(Link link, PageRequest pageable) {
        UriComponentsBuilder builder = fromUri(link.getTemplate().expand());
        new HateoasPageableHandlerMethodArgumentResolver().enhance(builder, null, pageable);
        return new Link(of(builder.build().toString()), SELF);
    }
}
