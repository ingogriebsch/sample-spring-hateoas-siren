package org.springframework.hateoas.mediatype.siren;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

public class SimpleSirenEntityClassProvider implements SirenEntityClassProvider {

    @Override
    public List<String> get(@NonNull RepresentationModel<?> model) {
        return newArrayList(uncapitalize(substringBeforeLast(model.getClass().getSimpleName(), "Model")));
    }

}
