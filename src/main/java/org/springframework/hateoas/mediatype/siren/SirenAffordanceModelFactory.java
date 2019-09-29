package org.springframework.hateoas.mediatype.siren;

import static org.springframework.hateoas.mediatype.siren.MediaTypes.SIREN_JSON;

import java.util.List;

import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.AffordanceModel.PayloadMetadata;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.QueryParameter;
import org.springframework.hateoas.mediatype.AffordanceModelFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class SirenAffordanceModelFactory implements AffordanceModelFactory {

    @Override
    public MediaType getMediaType() {
        return SIREN_JSON;
    }

    @Override
    public AffordanceModel getAffordanceModel(String name, Link link, HttpMethod httpMethod, InputPayloadMetadata inputType,
        List<QueryParameter> queryMethodParameters, PayloadMetadata outputType) {
        return new SirenAffordanceModel(name, link, httpMethod, inputType, queryMethodParameters, outputType);
    }

}
