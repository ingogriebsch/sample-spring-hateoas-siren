package org.springframework.hateoas.mediatype.siren;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.mediatype.siren.SirenLink.builder;
import static org.springframework.hateoas.mediatype.siren.SirenLink.TitleResolvable.of;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenLinkConverter {

    @NonNull
    private final MessageResolver messageResolver;

    public List<SirenLink> convert(@NonNull Iterable<Link> links) {
        return stream(links.spliterator(), false).map(l -> convert(l)).collect(toList());
    }

    public List<SirenLink> convert(@NonNull Link... links) {
        return convert(asList(links));
    }

    public SirenLink convert(@NonNull Link link) {
        return builder().rels(newArrayList(link.getRel().value())).href(link.getHref()).title(title(link)).build();
    }

    private String title(Link link) {
        return link.getTitle() != null ? link.getTitle() : messageResolver.resolve(of(link.getRel()));
    }
}
