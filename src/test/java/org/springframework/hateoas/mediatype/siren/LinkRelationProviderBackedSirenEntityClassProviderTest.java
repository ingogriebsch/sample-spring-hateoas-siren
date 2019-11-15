package org.springframework.hateoas.mediatype.siren;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;

public class LinkRelationProviderBackedSirenEntityClassProviderTest {

    private static final LinkRelationProvider linkRelationProvider = new DefaultLinkRelationProvider();
    private static final SirenEntityClassProvider sirenEntityClassProvider =
        new LinkRelationProviderBackedSirenEntityClassProvider(linkRelationProvider);

    @Test
    public void get_should_throw_exception_if_input_is_null() {
        assertThrows(IllegalArgumentException.class, () -> sirenEntityClassProvider.get(null));
    }

    @Test
    public void get_should_return_single_class_on_representation_model() {
        assertThat(sirenEntityClassProvider.get(new RepresentationModel<>()))
            .containsExactly(linkRelationProvider.getItemResourceRelFor(RepresentationModel.class).value());
    }

    @Test
    public void get_should_return_single_class_on_entity_model() {
        assertThat(sirenEntityClassProvider.get(new EntityModel<>("content")))
            .containsExactly(linkRelationProvider.getItemResourceRelFor(EntityModel.class).value());
    }

    @Test
    public void get_should_return_single_class_on_collection_model() {
        assertThat(sirenEntityClassProvider.get(new CollectionModel<>(newArrayList())))
            .containsExactly(linkRelationProvider.getItemResourceRelFor(CollectionModel.class).value());
    }

    @Test
    public void get_should_return_single_class_on_paged_model() {
        assertThat(sirenEntityClassProvider.get(new PagedModel<>(newArrayList(), new PageMetadata(0, 0, 0))))
            .containsExactly(linkRelationProvider.getItemResourceRelFor(PagedModel.class).value());
    }

}
