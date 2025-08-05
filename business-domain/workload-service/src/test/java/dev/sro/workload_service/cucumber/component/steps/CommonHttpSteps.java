package dev.sro.workload_service.cucumber.component.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public abstract class CommonHttpSteps {

    @Autowired
    protected WebTestClient webTestClient;

    protected void sendGetRequest(String endpoint, String token, Class<?> responseType,
            StepResponseHandler responseHandler) {
        var request = webTestClient.get()
                .uri(endpoint);

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        request.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendGetRequest(String endpoint, String token,
            org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var request = webTestClient.get()
                .uri(endpoint);

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        request.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPostRequest(String endpoint, Object body, Class<?> responseType,
            StepResponseHandler responseHandler) {
        webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPostRequest(String endpoint, Object body, String token, Class<?> responseType,
            StepResponseHandler responseHandler) {
        var webRequest = webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPostRequest(String endpoint, Object body, String token,
            org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPutRequest(String endpoint, Object body, Class<?> responseType,
            StepResponseHandler responseHandler) {
        webTestClient.put()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPutRequest(String endpoint, Object body, String token, Class<?> responseType,
            StepResponseHandler responseHandler) {
        var webRequest = webTestClient.put()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPutRequest(String endpoint, Object body, String token,
            org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.put()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPatchRequest(String endpoint, Object body, Class<?> responseType,
            StepResponseHandler responseHandler) {
        webTestClient.patch()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPatchRequest(String endpoint, Object body, String token, Class<?> responseType,
            StepResponseHandler responseHandler) {
        var webRequest = webTestClient.patch()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendPatchRequest(String endpoint, Object body, String token,
            org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.patch()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendDeleteRequest(String endpoint, String token, Class<?> responseType,
            StepResponseHandler responseHandler) {
        var request = webTestClient.delete()
                .uri(endpoint);

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        request.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendDeleteRequest(String endpoint, String token,
            org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var request = webTestClient.delete()
                .uri(endpoint);

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        request.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendDeleteRequest(String endpoint, Object body, String token, Class<?> responseType,
            StepResponseHandler responseHandler) {
        var webRequest = webTestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    protected void sendDeleteRequest(String endpoint, Object body, String token,
            org.springframework.core.ParameterizedTypeReference<?> responseType, StepResponseHandler responseHandler) {
        var webRequest = webTestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);

        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }

        webRequest.exchange()
                .expectBody(responseType)
                .consumeWith(result -> {
                    if (responseHandler != null) {
                        responseHandler.handleResponse(result.getStatus().value(), result.getResponseBody());
                    }
                });
    }

    @FunctionalInterface
    protected interface StepResponseHandler {
        void handleResponse(int status, Object responseBody);
    }
}