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

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

public class SirenCollectionModelSerializer extends AbstractSirenSerializer<CollectionModel<?>> {

    private static final long serialVersionUID = 9054285190464802945L;

    public SirenCollectionModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, messageResolver, null);
    }

    public SirenCollectionModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull MessageResolver messageResolver, BeanProperty property) {
        super(CollectionModel.class, sirenConfiguration, messageResolver, property);
    }

    @Override
    protected SirenEntity convert(CollectionModel<?> model, MessageResolver messageResolver) {
        return new SirenCollectionModelConverter(
            new SirenEntityModelConverter(new SirenLinkConverter(messageResolver), messageResolver),
            new SirenLinkConverter(messageResolver), messageResolver).convert(model);
    }

    @Override
    protected JsonSerializer<?> newInstance(SirenConfiguration sirenConfiguration, MessageResolver messageResolver,
        BeanProperty property) {
        return new SirenCollectionModelSerializer(sirenConfiguration, messageResolver, property);
    }

}
