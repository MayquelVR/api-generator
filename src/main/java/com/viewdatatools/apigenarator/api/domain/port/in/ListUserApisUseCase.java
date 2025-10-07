package com.viewdatatools.apigenarator.api.domain.port.in;

import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;

import java.util.List;

public interface ListUserApisUseCase {
    List<ApiDomain> listApisByUsername(String username);
}
