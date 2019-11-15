package org.springframework.hateoas.mediatype.siren;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

public interface SirenEntityClassProvider {

    List<String> get(RepresentationModel<?> model);

}
