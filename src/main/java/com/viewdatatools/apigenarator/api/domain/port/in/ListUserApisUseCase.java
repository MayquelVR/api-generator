package com.viewdatatools.apigenarator.api.domain.port.in;

import com.viewdatatools.apigenarator.api.domain.model.Api;
import java.util.List;

public interface ListUserApisUseCase {
    List<Api> listApisByUsername(String username);
}
