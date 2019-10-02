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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;

public class Jackson2SirenModule extends SimpleModule {

    private static final long serialVersionUID = 7377778164657569053L;

    public Jackson2SirenModule() {
        super("siren-module", new Version(1, 0, 0, null, "com.github.ingogriebsch.samples", "spring-hateoas-siren"));

        setMixInAnnotation(RepresentationModel.class, RepresentationModelMixIn.class);
        setMixInAnnotation(EntityModel.class, EntityModelMixIn.class);
        setMixInAnnotation(CollectionModel.class, CollectionModelMixIn.class);
        setMixInAnnotation(PagedModel.class, PagedModelMixIn.class);
    }

}
