package com.votacion.sistema_votacion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    private static final String VERIFY_URL =
        "https://www.google.com/recaptcha/api/siteverify";

    public boolean verificar(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = VERIFY_URL + "?secret=" + secretKey + "&response=" + token;
            Map response = restTemplate.postForObject(url, null, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("success"));
        } catch (Exception e) {
            return false;
        }
    }
}