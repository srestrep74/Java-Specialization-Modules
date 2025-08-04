package dev.sro.gym_service.cucumber.component.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class CommonHttpSteps {

    @Autowired
    protected WebTestClient webTestClient;

    // Common step for GET requests - can be overridden by specific implementations
    protected void sendGetRequest(String endpoint, String token, Class<?> responseType, StepResponseHandler responseHandler) {
        var request = webTestClient.get().uri(endpoint);
        
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        
        request.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for GET requests with ParameterizedTypeReference
    protected void sendGetRequest(String endpoint, String token, org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var request = webTestClient.get().uri(endpoint);
        
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        
        request.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for POST requests
    protected void sendPostRequest(String endpoint, Object body, Class<?> responseType, StepResponseHandler responseHandler) {
        webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for POST requests with ParameterizedTypeReference
    protected void sendPostRequest(String endpoint, Object body, org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for PUT requests
    protected void sendPutRequest(String endpoint, Object body, String token, Class<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.put()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
        
        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }
        
        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for PUT requests with ParameterizedTypeReference
    protected void sendPutRequest(String endpoint, Object body, String token, org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.put()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
        
        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }
        
        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for PATCH requests
    protected void sendPatchRequest(String endpoint, Object body, String token, Class<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.patch()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
        
        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }
        
        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for PATCH requests with ParameterizedTypeReference
    protected void sendPatchRequest(String endpoint, Object body, String token, org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.patch()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
        
        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }
        
        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for DELETE requests
    protected void sendDeleteRequest(String endpoint, String token, Class<?> responseType, StepResponseHandler responseHandler) {
        var request = webTestClient.delete().uri(endpoint);
        
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        
        request.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Common step for DELETE requests with ParameterizedTypeReference
    protected void sendDeleteRequest(String endpoint, String token, org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var request = webTestClient.delete().uri(endpoint);
        
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        
        request.exchange()
                .expectBody(responseType)
                .consumeWith(response -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(response.getStatus().value(), response.getResponseBody());
                    }
                });
    }

    // Functional interface for handling responses
    @FunctionalInterface
    protected interface StepResponseHandler {
        void handleResponse(int status, Object responseBody);
    }
} 