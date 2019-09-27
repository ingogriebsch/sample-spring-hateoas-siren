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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.IanaLinkRelations.SELF;

import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;

public class Jackson2SirenModuleTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll() {
        LinkRelationProvider linkRelationProvider =
            new DelegatingLinkRelationProvider(new AnnotationLinkRelationProvider(), new DefaultLinkRelationProvider());
        SirenMediaTypeConfiguration sirenMediaTypeConfiguration =
            new SirenMediaTypeConfiguration(linkRelationProvider, new SimpleObjectProvider<>(new SirenConfiguration()));
        objectMapper = sirenMediaTypeConfiguration.configureObjectMapper(new ObjectMapper());
    }

    @Nested
    class Serialize {

        @Nested
        class Entity {

            @Test
            public void should_return_matching_class_and_properties() throws Exception {
                EntityModel<Person> source = new EntityModel<>(new Person("Peter", 42));
                String expected = "{\"class\":[\"person\"],\"properties\":{\"name\":\"Peter\",\"age\":42}}";

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            public void should_return_matching_class_and_properties_and_self_link() throws Exception {
                EntityModel<Person> source = new EntityModel<>(new Person("Peter", 42), new Link("/persons/1", SELF));
                String expected =
                    "{\"class\":[\"person\"],\"properties\":{\"name\":\"Peter\",\"age\":42},\"links\":[{\"rel\":[\"self\"],\"href\":\"/persons/1\"}]}";

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

    }

    @Nested
    @Disabled
    class Deserialize {

        @Nested
        class Entity {

            @Test
            public void should_return_matching_pojo() throws Exception {
                String source = "{\"class\":[\"person\"],\"properties\":{\"name\":\"Peter\",\"age\":42}}";
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Person.class);
                EntityModel<Person> expected = new EntityModel<>(new Person("Peter", 42));

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
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
}
