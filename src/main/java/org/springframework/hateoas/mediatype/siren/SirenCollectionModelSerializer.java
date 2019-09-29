package org.springframework.hateoas.mediatype.siren;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import org.springframework.hateoas.CollectionModel;

public class SirenCollectionModelSerializer extends ContainerSerializer<CollectionModel<?>> implements ContextualSerializer {

    private static final long serialVersionUID = 9054285190464802945L;
    private final BeanProperty property;

    public SirenCollectionModelSerializer() {
        this(null);
    }

    public SirenCollectionModelSerializer(BeanProperty property) {
        super(CollectionModel.class, false);
        this.property = property;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenCollectionModelSerializer(property);
    }

    @Override
    public void serialize(CollectionModel<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenDocument doc = null;
        // SirenDocument.builder().classes(classes(value)).content(value.getContent()).links(links(value)).build();
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
    public boolean hasSingleElement(CollectionModel<?> value) {
        return false;
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return null;
    }

}
