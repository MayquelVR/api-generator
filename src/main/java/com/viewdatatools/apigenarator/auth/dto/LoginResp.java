package com.viewdatatools.apigenarator.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResp {
    private String username;
    private String email;
    private String token;
}
