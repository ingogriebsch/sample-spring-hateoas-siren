package org.springframework.hateoas.mediatype.siren;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.springframework.hateoas.mediatype.siren.SirenEntity.builder;
import static org.springframework.hateoas.mediatype.siren.SirenEntity.TitleResolvable.of;

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenEntityModelConverter {

    @NonNull
    private final SirenLinkConverter sirenLinkConverter;
    @NonNull
    private final MessageResolver messageResolver;

    public SirenEntity convert(@NonNull EntityModel<?> model) {
        return convert(model, null);
    }

    public SirenEntity convert(@NonNull EntityModel<?> model, List<String> rels) {
        return builder().classes(classes(model)).properties(properties(model)).links(sirenLinkConverter.convert(model.getLinks()))
            .rels(rels).title(messageResolver.resolve(of(model.getContent().getClass()))).build();
    }

    private static List<String> classes(EntityModel<?> model) {
        return newArrayList(uncapitalize(model.getContent().getClass().getSimpleName()));
    }

    private static Object properties(EntityModel<?> model) {
        return model.getContent();
    }
}
