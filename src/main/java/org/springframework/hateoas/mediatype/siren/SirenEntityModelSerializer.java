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

import static java.util.stream.Collectors.toList;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class SirenEntityModelSerializer extends ContainerSerializer<EntityModel<?>> implements ContextualSerializer {

    private static final long serialVersionUID = 2893716845519287714L;
    private final BeanProperty property;

    public SirenEntityModelSerializer() {
        this(null);
    }

    public SirenEntityModelSerializer(BeanProperty property) {
        super(EntityModel.class, false);
        this.property = property;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenEntityModelSerializer(property);
    }

    @Override
    public void serialize(EntityModel<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenDocument doc =
            SirenDocument.builder().classes(classes(value)).content(value.getContent()).links(links(value)).build();
        provider.findValueSerializer(SirenDocument.class, property).serialize(doc, gen, provider);
    }

    @Override
    public JavaType getContentType() {
        return null;
    }

    @Override
    public JsonSerializer<?> getContentSerializer() {
        return null;
    }

    @Override
    public boolean hasSingleElement(EntityModel<?> value) {
        return false;
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return null;
    }

    private List<String> classes(EntityModel<?> value) {
        return newArrayList(uncapitalize(value.getContent().getClass().getSimpleName()));
    }

    private List<SirenLink> links(EntityModel<?> value) {
        return value.getLinks().stream().map(l -> link(l)).collect(toList());
    }

    private SirenLink link(Link link) {
        return SirenLink.builder().rels(newArrayList(link.getRel().value())).href(link.getHref()).build();
    }

}
