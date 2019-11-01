package org.springframework.hateoas.mediatype.siren;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

public abstract class AbstractSirenDeserializer<T extends RepresentationModel<?>> extends ContainerDeserializerBase<T>
    implements ContextualDeserializer {

    private static final long serialVersionUID = 3796755247545654672L;

    protected final SirenConfiguration sirenConfiguration;
    protected final SirenLinkConverter linkConverter;
    protected final SirenAffordanceModelConverter affordanceModelConverter;
    protected final MessageResolver messageResolver;
    protected final JavaType contentType;

    protected AbstractSirenDeserializer(SirenConfiguration sirenConfiguration, SirenLinkConverter linkConverter,
        SirenAffordanceModelConverter affordanceModelConverter, MessageResolver messageResolver, JavaType contentType) {
        super(contentType);
        this.sirenConfiguration = sirenConfiguration;
        this.linkConverter = linkConverter;
        this.affordanceModelConverter = affordanceModelConverter;
        this.messageResolver = messageResolver;
        this.contentType = contentType;
    }

    @Override
    public JavaType getContentType() {
        return contentType;
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return null;
    }

}
