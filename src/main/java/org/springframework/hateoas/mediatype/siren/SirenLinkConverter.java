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
import static java.util.stream.StreamSupport.stream;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.mediatype.siren.SirenConfiguration.RenderTemplatedLinks.AS_LINK;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SirenLinkConverter {

    @NonNull
    private final SirenConfiguration sirenConfiguration;
    @NonNull
    private final MessageResolver messageResolver;

    public List<SirenLink> to(@NonNull Iterable<Link> links) {
        return stream(links.spliterator(), false).filter(l -> shouldConvert(l)).map(l -> convert(l)).collect(toList());
    }

    public List<Link> from(@NonNull Iterable<SirenLink> links) {
        return stream(links.spliterator(), false).map(l -> convert(l)).collect(toList());
    }

    private Link convert(SirenLink link) {
        String rel = link.getRels().stream().findFirst().orElse(null);
        return new Link(link.getHref(), rel).withTitle(link.getTitle()).withType(link.getType());
    }

    private SirenLink convert(@NonNull Link link) {
        return SirenLink.builder() //
            .rels(newArrayList(link.getRel().value())) //
            .href(link.getHref()).title(title(link)) //
            .type(link.getType()) //
            .build();
    }

    private String title(Link link) {
        String title = link.getTitle();
        if (title != null) {
            return title;
        }

        LinkRelation rel = link.getRel();
        if (rel != null) {
            return messageResolver.resolve(SirenLink.TitleResolvable.of(link.getRel()));
        }
        return null;
    }

    private boolean shouldConvert(Link link) {
        return link.isTemplated() ? sirenConfiguration.shouldRenderTemplatedLinksAs(AS_LINK) : true;
    }
}
