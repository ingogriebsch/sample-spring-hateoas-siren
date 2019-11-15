package org.springframework.hateoas.mediatype.siren;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.LinkRelationProvider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinkRelationProviderBackedSirenEntityClassProvider implements SirenEntityClassProvider {

    @NonNull
    private final LinkRelationProvider linkRelationProvider;

    @Override
    public List<String> get(@NonNull RepresentationModel<?> model) {
        Class<?> modelType = model.getClass();
        LinkRelation linkRelation = linkRelationProvider.getItemResourceRelFor(modelType);
        return newArrayList(linkRelation.value());
    }

}
