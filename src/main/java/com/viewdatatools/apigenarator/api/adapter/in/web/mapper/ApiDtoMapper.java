package com.viewdatatools.apigenarator.api.adapter.in.web.mapper;

import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;
import com.viewdatatools.apigenarator.api.dto.ApiCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class ApiDtoMapper {

    public ApiDomain toDomain(ApiCreateRequest req, String username) {
        return ApiDomain.builder()
                .name(req.getName())
                .description(req.getDescription())
                .version(req.getVersion())
                .baseUrl(req.getBaseUrl())
                .method(req.getMethod())
                .requestFormat(req.getRequestFormat())
                .responseFormat(req.getResponseFormat())
                .route(req.getRoute())
                .username(username)
                .build();
    }
}
