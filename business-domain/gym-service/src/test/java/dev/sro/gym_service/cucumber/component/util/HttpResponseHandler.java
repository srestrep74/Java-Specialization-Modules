package dev.sro.gym_service.cucumber.component.util;

import dev.sro.gym_service.cucumber.component.TraineeTestContext;
import dev.sro.gym_service.util.response.ApiStandardResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Consumer;

public class HttpResponseHandler {

    private final WebTestClient webTestClient;
    private final TraineeTestContext testContext;

    public HttpResponseHandler(WebTestClient webTestClient, TraineeTestContext testContext) {
        this.webTestClient = webTestClient;
        this.testContext = testContext;
    }

    public <T> void handleResponse(WebTestClient.RequestHeadersSpec<?> request, 
                                 ParameterizedTypeReference<ApiStandardResponse<T>> responseType) {
        try {
            request.exchange()
                    .expectBody(responseType)
                    .consumeWith(response -> {
                        testContext.setLastResponseStatus(response.getStatus().value());
                        testContext.setLastResponse(response.getResponseBody());
                    });
        } catch (Exception e) {
            // Handle different types of exceptions
            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcre = (WebClientResponseException) e;
                testContext.setLastResponseStatus(wcre.getStatusCode().value());
                // You might want to parse the error response body here
            } else {
                // For other exceptions, set a generic error status
                testContext.setLastResponseStatus(500);
            }
            testContext.setLastResponse(null);
        }
    }

    public void handleVoidResponse(WebTestClient.RequestHeadersSpec<?> request) {
        try {
            request.exchange()
                    .expectBody(new ParameterizedTypeReference<ApiStandardResponse<Void>>() {})
                    .consumeWith(response -> {
                        testContext.setLastResponseStatus(response.getStatus().value());
                        testContext.setLastResponse(response.getResponseBody());
                    });
        } catch (Exception e) {
            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcre = (WebClientResponseException) e;
                testContext.setLastResponseStatus(wcre.getStatusCode().value());
            } else {
                testContext.setLastResponseStatus(500);
            }
            testContext.setLastResponse(null);
        }
    }

    public <T> void handleResponseWithCustomConsumer(WebTestClient.RequestHeadersSpec<?> request,
                                                   ParameterizedTypeReference<ApiStandardResponse<T>> responseType,
                                                   Consumer<ApiStandardResponse<T>> consumer) {
        try {
            request.exchange()
                    .expectBody(responseType)
                    .consumeWith(response -> {
                        testContext.setLastResponseStatus(response.getStatus().value());
                        testContext.setLastResponse(response.getResponseBody());
                        if (response.getResponseBody() != null) {
                            consumer.accept(response.getResponseBody());
                        }
                    });
        } catch (Exception e) {
            if (e instanceof WebClientResponseException) {
                WebClientResponseException wcre = (WebClientResponseException) e;
                testContext.setLastResponseStatus(wcre.getStatusCode().value());
            } else {
                testContext.setLastResponseStatus(500);
            }
            testContext.setLastResponse(null);
        }
    }
} 