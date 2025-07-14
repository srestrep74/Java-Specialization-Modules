package dev.sro.gym_service.util.response;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorResponseBuilderTest {

    private static final String MOCK_PATH = "/api/v1/test";

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        lenient().when(mockRequest.getRequestURI()).thenReturn(MOCK_PATH);
    }

    @Test
    void buildErrorResponse_WithHttpServletRequest_ShouldReturnCorrectErrorResponse() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorType = "Validation Error";
        String message = "Invalid input data";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, errorType, message, mockRequest);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(status.value(), body.status());
        assertEquals(errorType, body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());

        verify(mockRequest).getRequestURI();
    }

    @Test
    void buildErrorResponse_WithHttpServletRequestAndNullMessage_ShouldReturnErrorResponseWithNullMessage() {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String errorType = "Resource Not Found";
        String message = null;

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, errorType, message, mockRequest);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(status.value(), body.status());
        assertEquals(errorType, body.error());
        assertNull(body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void buildErrorResponse_WithHttpServletRequestAndNullErrorType_ShouldReturnErrorResponseWithNullErrorType() {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorType = null;
        String message = "Internal server error occurred";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, errorType, message, mockRequest);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(status.value(), body.status());
        assertNull(body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void buildErrorResponse_WithStringPath_ShouldReturnCorrectErrorResponse() {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String errorType = "Access Denied";
        String message = "You do not have permission to access this resource";
        String path = "/api/v1/admin/users";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, errorType, message, path);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(status.value(), body.status());
        assertEquals(errorType, body.error());
        assertEquals(message, body.message());
        assertEquals(path, body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void buildErrorResponse_WithStringPathAndNullValues_ShouldReturnErrorResponseWithNullValues() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorType = null;
        String message = null;
        String path = null;

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, errorType, message, path);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(status.value(), body.status());
        assertNull(body.error());
        assertNull(body.message());
        assertNull(body.path());
        assertNotNull(body.timestamp());
    }

    @ParameterizedTest
    @MethodSource("provideHttpStatusAndErrorType")
    void buildErrorResponse_WithDifferentHttpStatuses_ShouldReturnCorrectStatus(HttpStatus status,
            String expectedErrorType) {
        String message = "Test message for " + status.name();

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, expectedErrorType, message, mockRequest);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(status.value(), body.status());
        assertEquals(expectedErrorType, body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
    }

    private static Stream<Arguments> provideHttpStatusAndErrorType() {
        return Stream.of(
                Arguments.of(HttpStatus.BAD_REQUEST, "Bad Request"),
                Arguments.of(HttpStatus.UNAUTHORIZED, "Unauthorized"),
                Arguments.of(HttpStatus.FORBIDDEN, "Forbidden"),
                Arguments.of(HttpStatus.NOT_FOUND, "Not Found"),
                Arguments.of(HttpStatus.CONFLICT, "Conflict"),
                Arguments.of(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity"),
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
                Arguments.of(HttpStatus.BAD_GATEWAY, "Bad Gateway"),
                Arguments.of(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable"));
    }

    @Test
    void notFound_WithMessage_ShouldReturnNotFoundResponse() {
        String message = "Resource not found";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.notFound(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("Resource Not Found", body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());

        verify(mockRequest).getRequestURI();
    }

    @Test
    void notFound_WithNullMessage_ShouldReturnNotFoundResponseWithNullMessage() {
        String message = null;

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.notFound(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("Resource Not Found", body.error());
        assertNull(body.message());
        assertEquals(MOCK_PATH, body.path());
    }

    @Test
    void conflict_WithMessage_ShouldReturnConflictResponse() {
        String message = "Resource already exists";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.conflict(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("Resource Conflict", body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());

        verify(mockRequest).getRequestURI();
    }

    @Test
    void conflict_WithNullMessage_ShouldReturnConflictResponseWithNullMessage() {
        String message = null;

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.conflict(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("Resource Conflict", body.error());
        assertNull(body.message());
        assertEquals(MOCK_PATH, body.path());
    }

    @Test
    void validationError_WithMessage_ShouldReturnBadRequestResponse() {
        String message = "Invalid input data";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.validationError(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Validation Error", body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());

        verify(mockRequest).getRequestURI();
    }

    @Test
    void validationError_WithNullMessage_ShouldReturnBadRequestResponseWithNullMessage() {
        String message = null;

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.validationError(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Validation Error", body.error());
        assertNull(body.message());
        assertEquals(MOCK_PATH, body.path());
    }

    @Test
    void unauthorized_WithMessage_ShouldReturnUnauthorizedResponse() {
        String message = "Authentication required";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.unauthorized(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(401, body.status());
        assertEquals("Unauthorized", body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());

        verify(mockRequest).getRequestURI();
    }

    @Test
    void unauthorized_WithNullMessage_ShouldReturnUnauthorizedResponseWithNullMessage() {
        String message = null;

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.unauthorized(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(401, body.status());
        assertEquals("Unauthorized", body.error());
        assertNull(body.message());
        assertEquals(MOCK_PATH, body.path());
    }

    @Test
    void internalServerError_WithMessage_ShouldReturnInternalServerErrorResponse() {
        String message = "Something went wrong";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.internalServerError(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.status());
        assertEquals("Internal Server Error", body.error());
        assertEquals(message, body.message());
        assertEquals(MOCK_PATH, body.path());
        assertNotNull(body.timestamp());

        verify(mockRequest).getRequestURI();
    }

    @Test
    void internalServerError_WithNullMessage_ShouldReturnInternalServerErrorResponseWithNullMessage() {
        String message = null;

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.internalServerError(message, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiStandardError body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.status());
        assertEquals("Internal Server Error", body.error());
        assertNull(body.message());
        assertEquals(MOCK_PATH, body.path());
    }

    @ParameterizedTest
    @ValueSource(strings = { "/api/v1/users", "/api/v1/trainers", "/api/v1/trainees", "/api/v1/trainings",
            "/admin/users" })
    void allMethods_WithDifferentPaths_ShouldReturnCorrectPath(String path) {
        when(mockRequest.getRequestURI()).thenReturn(path);
        String message = "Test message";

        ResponseEntity<ApiStandardError> notFoundResponse = ErrorResponseBuilder.notFound(message, mockRequest);
        ResponseEntity<ApiStandardError> conflictResponse = ErrorResponseBuilder.conflict(message, mockRequest);
        ResponseEntity<ApiStandardError> validationResponse = ErrorResponseBuilder.validationError(message,
                mockRequest);
        ResponseEntity<ApiStandardError> unauthorizedResponse = ErrorResponseBuilder.unauthorized(message, mockRequest);
        ResponseEntity<ApiStandardError> internalServerErrorResponse = ErrorResponseBuilder.internalServerError(message,
                mockRequest);

        assertEquals(path, notFoundResponse.getBody().path());
        assertEquals(path, conflictResponse.getBody().path());
        assertEquals(path, validationResponse.getBody().path());
        assertEquals(path, unauthorizedResponse.getBody().path());
        assertEquals(path, internalServerErrorResponse.getBody().path());
    }

    @Test
    void allConvenienceMethods_ShouldHaveCorrectErrorTypesAndStatusCodes() {
        String message = "Test message";

        ResponseEntity<ApiStandardError> notFoundResponse = ErrorResponseBuilder.notFound(message, mockRequest);
        ResponseEntity<ApiStandardError> conflictResponse = ErrorResponseBuilder.conflict(message, mockRequest);
        ResponseEntity<ApiStandardError> validationResponse = ErrorResponseBuilder.validationError(message,
                mockRequest);
        ResponseEntity<ApiStandardError> unauthorizedResponse = ErrorResponseBuilder.unauthorized(message, mockRequest);
        ResponseEntity<ApiStandardError> internalServerErrorResponse = ErrorResponseBuilder.internalServerError(message,
                mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
        assertEquals(HttpStatus.CONFLICT, conflictResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, validationResponse.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedResponse.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, internalServerErrorResponse.getStatusCode());

        assertEquals("Resource Not Found", notFoundResponse.getBody().error());
        assertEquals("Resource Conflict", conflictResponse.getBody().error());
        assertEquals("Validation Error", validationResponse.getBody().error());
        assertEquals("Unauthorized", unauthorizedResponse.getBody().error());
        assertEquals("Internal Server Error", internalServerErrorResponse.getBody().error());

        assertEquals(404, notFoundResponse.getBody().status());
        assertEquals(409, conflictResponse.getBody().status());
        assertEquals(400, validationResponse.getBody().status());
        assertEquals(401, unauthorizedResponse.getBody().status());
        assertEquals(500, internalServerErrorResponse.getBody().status());
    }

    @Test
    void allMethods_ShouldSetTimestampCorrectly() {
        String message = "Test message";

        ResponseEntity<ApiStandardError> directResponse = ErrorResponseBuilder.buildErrorResponse(
                HttpStatus.BAD_REQUEST, "Test Error", message, mockRequest);
        ResponseEntity<ApiStandardError> notFoundResponse = ErrorResponseBuilder.notFound(message, mockRequest);
        ResponseEntity<ApiStandardError> conflictResponse = ErrorResponseBuilder.conflict(message, mockRequest);
        ResponseEntity<ApiStandardError> validationResponse = ErrorResponseBuilder.validationError(message,
                mockRequest);
        ResponseEntity<ApiStandardError> unauthorizedResponse = ErrorResponseBuilder.unauthorized(message, mockRequest);
        ResponseEntity<ApiStandardError> internalServerErrorResponse = ErrorResponseBuilder.internalServerError(message,
                mockRequest);

        assertNotNull(directResponse.getBody().timestamp());
        assertNotNull(notFoundResponse.getBody().timestamp());
        assertNotNull(conflictResponse.getBody().timestamp());
        assertNotNull(validationResponse.getBody().timestamp());
        assertNotNull(unauthorizedResponse.getBody().timestamp());
        assertNotNull(internalServerErrorResponse.getBody().timestamp());
    }

    @Test
    void allMethods_ShouldPreserveOriginalMessage() {
        String originalMessage = "This is the original error message";

        ResponseEntity<ApiStandardError> notFoundResponse = ErrorResponseBuilder.notFound(originalMessage, mockRequest);
        ResponseEntity<ApiStandardError> conflictResponse = ErrorResponseBuilder.conflict(originalMessage, mockRequest);
        ResponseEntity<ApiStandardError> validationResponse = ErrorResponseBuilder.validationError(originalMessage,
                mockRequest);
        ResponseEntity<ApiStandardError> unauthorizedResponse = ErrorResponseBuilder.unauthorized(originalMessage,
                mockRequest);
        ResponseEntity<ApiStandardError> internalServerErrorResponse = ErrorResponseBuilder
                .internalServerError(originalMessage, mockRequest);

        assertEquals(originalMessage, notFoundResponse.getBody().message());
        assertEquals(originalMessage, conflictResponse.getBody().message());
        assertEquals(originalMessage, validationResponse.getBody().message());
        assertEquals(originalMessage, unauthorizedResponse.getBody().message());
        assertEquals(originalMessage, internalServerErrorResponse.getBody().message());
    }

    @Test
    void buildErrorResponse_WithDifferentPathFormats_ShouldHandleCorrectly() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorType = "Test Error";
        String message = "Test message";
        String[] paths = {
                "/api/v1/users",
                "/api/v1/users/123",
                "/api/v1/users/123/profile",
                "/",
                "",
                "/api/v1/users?param=value",
                "/api/v1/users#section"
        };

        for (String path : paths) {
            ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                    status, errorType, message, path);

            assertNotNull(response);
            assertEquals(status, response.getStatusCode());
            assertEquals(path, response.getBody().path());
        }
    }

    @Test
    void buildErrorResponse_WithLongMessage_ShouldHandleLongMessageCorrectly() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorType = "Validation Error";
        String longMessage = "This is a very long error message that contains a lot of details about what went wrong. "
                +
                "It includes multiple sentences and provides comprehensive information about the error condition. " +
                "The message should be preserved exactly as provided, regardless of its length or content.";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, errorType, longMessage, mockRequest);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(longMessage, response.getBody().message());
    }

    @Test
    void buildErrorResponse_WithSpecialCharactersInMessage_ShouldHandleSpecialCharacters() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorType = "Validation Error";
        String specialMessage = "Error with special characters: áéíóú, ñ, ü, @#$%^&*()[]{}|\\:;\"'<>,.?/~`";

        ResponseEntity<ApiStandardError> response = ErrorResponseBuilder.buildErrorResponse(
                status, errorType, specialMessage, mockRequest);

        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
        assertEquals(specialMessage, response.getBody().message());
    }
}