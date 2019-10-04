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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpMethod.GET;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

@Builder
@JsonPropertyOrder({ "name", "class", "method", "href", "title", "type", "fields" })
@Value
public class SirenAction {

    @NonNull
    private String name;

    @JsonInclude(NON_EMPTY)
    @JsonProperty("class")
    @Singular
    private List<String> classes;

    @Default
    @JsonInclude(NON_NULL)
    @NonNull
    private HttpMethod method = GET;

    @NonNull
    private String href;

    @JsonInclude(NON_NULL)
    private String title;

    @JsonInclude(NON_NULL)
    private String type;

    @JsonInclude(NON_EMPTY)
    @Singular
    private List<Field> fields;

    @Builder
    @JsonPropertyOrder({ "name", "class", "type", "value", "title" })
    @Value
    public static final class Field {

        @NonNull
        private String name;

        @JsonInclude(NON_EMPTY)
        @JsonProperty("class")
        @Singular
        private List<String> classes;

        @JsonInclude(NON_NULL)
        private String type;

        @JsonInclude(NON_NULL)
        private Object value;

        @JsonInclude(NON_NULL)
        private String title;

        @Value(staticConstructor = "of")
        public static final class TitleResolvable implements MessageSourceResolvable {

            private String name;

            @Override
            public String[] getCodes() {
                return Stream.of(name, "default").map(it -> String.format("_field.%s.title", it)).toArray(String[]::new);
            }
        }
    }

    @Value(staticConstructor = "of")
    public static final class TitleResolvable implements MessageSourceResolvable {

        private String name;

        @Override
        public String[] getCodes() {
            return Stream.of(name, "default").map(it -> String.format("_action.%s.title", it)).toArray(String[]::new);
        }
    }
}
