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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SirenConfiguration {

    @NonNull
    private RenderTemplatedLinks renderTemplatedLinks = RenderTemplatedLinks.AS_ACTION;

    public boolean shouldRenderTemplatedLinksAs(@NonNull RenderTemplatedLinks renderTemplatedLinks) {
        return this.renderTemplatedLinks.equals(renderTemplatedLinks);
    }

    public static enum RenderTemplatedLinks {
            AS_LINK, AS_ACTION
    }

}
