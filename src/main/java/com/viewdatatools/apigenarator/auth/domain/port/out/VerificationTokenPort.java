package com.viewdatatools.apigenarator.auth.domain.port.out;

import lombok.Getter;

public interface VerificationTokenPort {
    void create(String username, String email, String password, String tokenHash);
    VerificationTokenData validate(String tokenHash);
    void delete(String tokenHash);

    @Getter
    class VerificationTokenData {
        private final String username;
        private final String email;
        private final String password;

        public VerificationTokenData(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

    }
}

