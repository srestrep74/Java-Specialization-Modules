package dev.sro.workload_service.util.response;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseBuilderTest {

    private MockHttpServletRequest request;
    private final String testPath = "/test-path";

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setRequestURI(testPath);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testBuildErrorResponseWithPath() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorType = "Test Error";
        String message = "This is a test error";

        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.buildErrorResponse(status, errorType,
                message, testPath);
        ApiStandardError errorBody = responseEntity.getBody();

        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(errorBody);
        assertAll(
                () -> assertEquals(status.value(), errorBody.status()),
                () -> assertEquals(errorType, errorBody.error()),
                () -> assertEquals(message, errorBody.message()),
                () -> assertEquals(testPath, errorBody.path()));
    }

    @Test
    void testBuildErrorResponseWithRequest() {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorType = "Server Error";
        String message = "This is a server error";

        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.buildErrorResponse(status, errorType,
                message, (HttpServletRequest) request);
        ApiStandardError errorBody = responseEntity.getBody();

        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(errorBody);
        assertAll(
                () -> assertEquals(status.value(), errorBody.status()),
                () -> assertEquals(errorType, errorBody.error()),
                () -> assertEquals(message, errorBody.message()),
                () -> assertEquals(testPath, errorBody.path()));
    }

    private void assertErrorResponse(ResponseEntity<ApiStandardError> responseEntity, HttpStatus expectedStatus,
            String expectedErrorType, String message) {
        ApiStandardError errorBody = responseEntity.getBody();

        assertEquals(expectedStatus, responseEntity.getStatusCode());
        assertNotNull(errorBody);
        assertAll(
                () -> assertEquals(expectedStatus.value(), errorBody.status()),
                () -> assertEquals(expectedErrorType, errorBody.error()),
                () -> assertEquals(message, errorBody.message()),
                () -> assertEquals(testPath, errorBody.path()));
    }

    @Test
    void testNotFound() {
        String message = "Resource not found";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.notFound(message, request);
        assertErrorResponse(responseEntity, HttpStatus.NOT_FOUND, "Resource Not Found", message);
    }

    @Test
    void testConflict() {
        String message = "Conflict occurred";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.conflict(message, request);
        assertErrorResponse(responseEntity, HttpStatus.CONFLICT, "Resource Conflict", message);
    }

    @Test
    void testValidationError() {
        String message = "Validation failed";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.validationError(message, request);
        assertErrorResponse(responseEntity, HttpStatus.BAD_REQUEST, "Validation Error", message);
    }

    @Test
    void testUnauthorized() {
        String message = "Unauthorized access";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.unauthorized(message, request);
        assertErrorResponse(responseEntity, HttpStatus.UNAUTHORIZED, "Unauthorized", message);
    }

    @Test
    void testForbidden() {
        String message = "Access forbidden";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.forbidden(message, request);
        assertErrorResponse(responseEntity, HttpStatus.FORBIDDEN, "Forbidden", message);
    }

    @Test
    void testBadRequest() {
        String message = "Bad request";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.badRequest(message, request);
        assertErrorResponse(responseEntity, HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    @Test
    void testInternalServerError() {
        String message = "Internal server error";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.internalServerError(message, request);
        assertErrorResponse(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }

    @Test
    void testServiceUnavailable() {
        String message = "Service unavailable";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.serviceUnavailable(message, request);
        assertErrorResponse(responseEntity, HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", message);
    }

    @Test
    void testTooManyRequests() {
        String message = "Too many requests";
        ResponseEntity<ApiStandardError> responseEntity = ErrorResponseBuilder.tooManyRequests(message, request);
        assertErrorResponse(responseEntity, HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", message);
    }
}