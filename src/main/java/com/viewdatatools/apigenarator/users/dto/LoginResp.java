package com.viewdatatools.apigenarator.users.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResp {
    private String username;
    private String email;
    private String token;
}
