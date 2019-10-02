package org.springframework.hateoas.mediatype.siren;

import static java.util.stream.Collectors.toList;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.mediatype.siren.SirenEntity.builder;
import static org.springframework.hateoas.mediatype.siren.SirenEntity.TitleResolvable.of;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenCollectionModelConverter {

    @NonNull
    private final SirenEntityModelConverter entityConverter;
    @NonNull
    private final SirenLinkConverter sirenLinkConverter;
    @NonNull
    private final MessageResolver messageResolver;

    public SirenEntity convert(@NonNull CollectionModel<?> model) {
        return builder().classes(classes(model)).properties(properties(model)).entities(entities(model))
            .links(sirenLinkConverter.convert(model.getLinks())).title(messageResolver.resolve(of(model.getContent().getClass())))
            .build();
    }

    protected Map<String, Object> properties(CollectionModel<?> model) {
        Map<String, Object> content = new HashMap<>();
        content.put("size", model.getContent().size());
        return content;
    }

    protected List<String> classes(CollectionModel<?> model) {
        return newArrayList("collection");
    }

    private List<SirenEmbeddable> entities(CollectionModel<?> model) {
        return model.getContent().stream().map(c -> entity(c)).collect(toList());
    }

    private SirenEmbeddable entity(Object embeddable) {
        if (!EntityModel.class.equals(embeddable.getClass())) {
            throw new IllegalArgumentException(String.format("Sub-entities must be of type '%s' [but is of type '%s']!",
                EntityModel.class.getName(), embeddable.getClass().getName()));
        }

        return entityConverter.convert((EntityModel<?>) embeddable, newArrayList("item"));
    }

}
