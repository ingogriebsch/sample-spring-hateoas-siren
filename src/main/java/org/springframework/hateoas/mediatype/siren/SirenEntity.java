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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@JsonPropertyOrder({ "class", "rel", "href", "properties", "entities", "links", "actions", "title" })
@Value
public class SirenEntity {

    @JsonProperty("rel")
    @NonNull
    private List<String> rels;

    @JsonProperty("nested")
    // FIXME https://stackoverflow.com/questions/30846404/jackson-jsonunwrapped-behaviour-with-custom-jsonserializer/30863670
    // FIXME https://michael-simons.github.io/simple-meetup/unwrapping-custom-jackson-serializer
    @JsonUnwrapped
    @JsonInclude(NON_NULL)
    private Object entity;

}
