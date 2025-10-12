package com.viewdatatools.apigenarator.auth.domain.port.out;

import lombok.Getter;

import java.util.UUID;

public interface VerificationTokenPort {
    void create(UUID uuid, String username, String email, String password, String tokenHash);
    VerificationTokenData validate(String tokenHash);
    void delete(String tokenHash);

    @Getter
    class VerificationTokenData {
        private final UUID uuid;
        private final String username;
        private final String email;
        private final String password;

        public VerificationTokenData(UUID uuid, String username, String email, String password) {
            this.uuid = uuid;
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }
}
