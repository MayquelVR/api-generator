package com.viewdatatools.apigenarator.api.domain.port.in;

import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;

public interface CreateApiUseCase {
    void createApi(ApiDomain apiDomain);
}
