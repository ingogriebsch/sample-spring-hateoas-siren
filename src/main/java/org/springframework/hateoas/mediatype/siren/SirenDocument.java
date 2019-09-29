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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Value;

@Builder
@JsonPropertyOrder({ "class", "properties", "entities", "links", "actions", "title" })
@Value
public class SirenDocument {

    @JsonInclude(NON_EMPTY)
    @JsonProperty("class")
    private List<String> classes;

    @JsonInclude(NON_NULL)
    @JsonProperty("properties")
    private Object content;

    @JsonInclude(NON_EMPTY)
    private List<SirenEntity> entities;

    @JsonInclude(NON_EMPTY)
    private List<SirenLink> links;

    @JsonInclude(NON_EMPTY)
    private List<SirenAction> actions;

    @JsonInclude(NON_NULL)
    private String title;

}
