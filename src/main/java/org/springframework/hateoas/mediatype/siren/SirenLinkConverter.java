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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import static com.google.common.collect.Lists.newArrayList;

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
        return SirenLink.builder().rels(newArrayList(link.getRel().value())).href(link.getHref()).title(title(link)).build();
    }

    private String title(Link link) {
        return link.getTitle() != null ? link.getTitle() : messageResolver.resolve(SirenLink.TitleResolvable.of(link.getRel()));
    }
}
