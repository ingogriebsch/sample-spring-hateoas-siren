package org.springframework.hateoas.mediatype.siren;

import java.util.List;

import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.QueryParameter;
import org.springframework.http.HttpMethod;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class SirenAffordanceModel extends AffordanceModel {

    public SirenAffordanceModel(String name, Link link, HttpMethod httpMethod, InputPayloadMetadata inputType,
        List<QueryParameter> queryMethodParameters, PayloadMetadata outputType) {
        super(name, link, httpMethod, inputType, queryMethodParameters, outputType);
    }

}
