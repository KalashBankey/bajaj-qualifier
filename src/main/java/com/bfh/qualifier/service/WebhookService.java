package com.bfh.qualifier.service;

import com.bfh.qualifier.model.WebhookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    @Value("${webhook.api.url}")
    private String webhookApiUrl;

    private String finalQuery = "SELECT \n" +
            "    p.AMOUNT AS SALARY,\n" +
            "    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,\n" +
            "    TIMESTAMPDIFF(YEAR, e.DOB, p.PAYMENT_TIME) AS AGE,\n" +
            "    d.DEPARTMENT_NAME\n" +
            "FROM \n" +
            "    PAYMENTS p\n" +
            "JOIN \n" +
            "    EMPLOYEE e ON p.EMP_ID = e.EMP_ID\n" +
            "JOIN \n" +
            "    DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID\n" +
            "WHERE \n" +
            "    DAY(p.PAYMENT_TIME) != 1\n" +
            "ORDER BY \n" +
            "    p.AMOUNT DESC\n" +
            "LIMIT 1;\n";

    public void process() {

        String regNo = "0827CI221073";
        String name = "Kalash Bankey";
        String email = "kalashbankey220655@acropolis.in";

        // Request body
        String requestBody = String.format("{\"name\": \"%s\", \"regNo\": \"%s\", \"email\": \"%s\"}", name, regNo, email);


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                webhookApiUrl + "/hiring/generateWebhook/JAVA",
                HttpMethod.POST,
                entity,
                WebhookResponse.class
        );


        if (response.getStatusCode() == HttpStatus.OK) {
            WebhookResponse webhookResponse = response.getBody();
            String webhookUrl = webhookResponse.getWebhook();
            String accessToken = webhookResponse.getAccessToken();


            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);


            submitFinalQuery(webhookUrl, accessToken);
        } else {
            System.out.println("Failed to generate webhook. Status: " + response.getStatusCode());
        }
    }

    private void submitFinalQuery(String webhookUrl, String accessToken) {

        String finalRequestBody = String.format("{\"finalQuery\": \"%s\"}", finalQuery);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);


        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(finalRequestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl + "/hiring/testWebhook/JAVA",
                HttpMethod.POST,
                entity,
                String.class
        );


        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Final query submitted successfully: " + response.getBody());
        } else {
            System.out.println("Failed to submit final query. Status: " + response.getStatusCode());
        }
    }
}
