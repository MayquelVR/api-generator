package com.viewdatatools.apigenarator.auth.domain.port.out;

public interface MailServicePort {
    void sendMail(String to, String subject, String body);
}

