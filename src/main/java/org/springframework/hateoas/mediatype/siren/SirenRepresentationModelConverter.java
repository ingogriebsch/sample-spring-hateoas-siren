package org.springframework.hateoas.mediatype.siren;

import static org.springframework.hateoas.mediatype.siren.SirenEntity.TitleResolvable.of;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenRepresentationModelConverter {

    @NonNull
    private final SirenLinkConverter sirenLinkConverter;
    @NonNull
    private final MessageResolver messageResolver;

    public SirenEntity convert(@NonNull RepresentationModel<?> model) {
        return SirenEntity.builder().links(sirenLinkConverter.convert(model.getLinks()))
            .title(messageResolver.resolve(of(model.getClass()))).build();
    }

}
