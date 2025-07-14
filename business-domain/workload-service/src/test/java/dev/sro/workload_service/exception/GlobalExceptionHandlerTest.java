package dev.sro.workload_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import dev.sro.workload_service.util.response.ApiStandardError;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    void shouldHandleTrainerNotFoundException() {
        String username = "john.doe";
        TrainerNotFoundException exception = new TrainerNotFoundException(username);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleTrainerNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(404, error.status());
        assertEquals("Resource Not Found", error.error());
        assertEquals("Trainer not found with username: " + username, error.message());
        assertEquals("/api/v1/test", error.path());
        assertNotNull(error.timestamp());
    }

    @Test
    void shouldHandleMonthlySummaryNotFoundException() {
        String username = "trainer.username";
        MonthlySummaryNotFoundException exception = new MonthlySummaryNotFoundException(username);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleMonthlySummaryNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(404, error.status());
        assertEquals("Resource Not Found", error.error());
        assertEquals("No monthly summary found for trainer: " + username, error.message());
        assertEquals("/api/v1/test", error.path());
    }

    @Test
    void shouldHandleWorkloadProcessingException() {
        String errorMessage = "Processing failed";
        WorkloadProcessingException exception = new WorkloadProcessingException(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleWorkloadProcessingException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(500, error.status());
        assertEquals("Internal Server Error", error.error());
        assertEquals(errorMessage, error.message());
        assertEquals("/api/v1/test", error.path());
    }

    @Test
    void shouldHandleInvalidWorkloadDataException() {
        String errorMessage = "Invalid data provided";
        InvalidWorkloadDataException exception = new InvalidWorkloadDataException(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleInvalidWorkloadDataException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(400, error.status());
        assertEquals("Validation Error", error.error());
        assertEquals(errorMessage, error.message());
        assertEquals("/api/v1/test", error.path());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "field1", "Field1 is required");
        FieldError fieldError2 = new FieldError("object", "field2", "Field2 must be positive");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError1, fieldError2));

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleMethodArgumentNotValidException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(400, error.status());
        assertEquals("Validation Error", error.error());
        assertTrue(error.message().contains("field1: Field1 is required"));
        assertTrue(error.message().contains("field2: Field2 must be positive"));
        assertEquals("/api/v1/test", error.path());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);

        when(violation1.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation1.getPropertyPath().toString()).thenReturn("username");
        when(violation1.getMessage()).thenReturn("must not be blank");

        when(violation2.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation2.getPropertyPath().toString()).thenReturn("duration");
        when(violation2.getMessage()).thenReturn("must be positive");

        when(exception.getConstraintViolations()).thenReturn(Set.of(violation1, violation2));

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleConstraintViolationException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(400, error.status());
        assertEquals("Validation Error", error.error());
        assertTrue(error.message().contains("username: must not be blank"));
        assertTrue(error.message().contains("duration: must be positive"));
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("duration");
        doReturn(Integer.class).when(exception).getRequiredType();

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleMethodArgumentTypeMismatchException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(400, error.status());
        assertEquals("Validation Error", error.error());
        assertEquals("Parameter 'duration' should be of type Integer", error.message());
        assertEquals("/api/v1/test", error.path());
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchExceptionWithNullRequiredType() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("param");
        when(exception.getRequiredType()).thenReturn(null);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleMethodArgumentTypeMismatchException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals("Parameter 'param' should be of type unknown", error.message());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleIllegalArgumentException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(400, error.status());
        assertEquals("Validation Error", error.error());
        assertEquals(errorMessage, error.message());
    }

    @Test
    void shouldHandleAuthenticationFailedExceptionWithAccountLocked() {
        String errorMessage = "Account locked due to multiple failed attempts";
        AuthenticationFailedException exception = new AuthenticationFailedException(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleAuthenticationFailedException(exception, request);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(429, error.status());
        assertEquals("Account Locked", error.error());
        assertEquals(errorMessage, error.message());
    }

    @Test
    void shouldHandleAuthenticationFailedExceptionWithAccountIsLocked() {
        String errorMessage = "Account is locked";
        AuthenticationFailedException exception = new AuthenticationFailedException(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleAuthenticationFailedException(exception, request);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(429, error.status());
        assertEquals("Account Locked", error.error());
        assertEquals(errorMessage, error.message());
    }

    @Test
    void shouldHandleAuthenticationFailedExceptionWithRegularMessage() {
        String errorMessage = "Invalid credentials";
        AuthenticationFailedException exception = new AuthenticationFailedException(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleAuthenticationFailedException(exception, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(401, error.status());
        assertEquals("Unauthorized", error.error());
        assertEquals(errorMessage, error.message());
    }

    @Test
    void shouldHandleAuthenticationException() {
        String errorMessage = "Authentication failed";
        AuthenticationException exception = new AuthenticationException(errorMessage) {
        };

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleAuthenticationException(exception, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(401, error.status());
        assertEquals("Unauthorized", error.error());
        assertEquals("Authentication failed: " + errorMessage, error.message());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        String errorMessage = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleAccessDeniedException(exception, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(403, error.status());
        assertEquals("Forbidden", error.error());
        assertEquals("Access denied: " + errorMessage, error.message());
    }

    @Test
    void shouldHandleGenericException() {
        String errorMessage = "Something went wrong";
        Exception exception = new Exception(errorMessage);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals(500, error.status());
        assertEquals("Internal Server Error", error.error());
        assertEquals("An unexpected error occurred: " + errorMessage, error.message());
    }

    @Test
    void shouldRethrowAccessDeniedExceptionInGenericHandler() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        assertThrows(AccessDeniedException.class, () -> {
            globalExceptionHandler.handleGenericException(exception, request);
        });
    }

    @Test
    void shouldHandleExceptionWithNullMessage() {
        Exception exception = new Exception((String) null);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals("An unexpected error occurred: null", error.message());
    }

    @Test
    void shouldHandleMethodArgumentNotValidExceptionWithNoFieldErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleMethodArgumentNotValidException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals("", error.message());
    }

    @Test
    void shouldHandleConstraintViolationExceptionWithNoViolations() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(exception.getConstraintViolations()).thenReturn(Set.of());

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleConstraintViolationException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertEquals("", error.message());
    }

    @Test
    void shouldHandleMultipleConstructorsOfCustomExceptions() {
        MonthlySummaryNotFoundException exception1 = new MonthlySummaryNotFoundException("trainer1");
        MonthlySummaryNotFoundException exception2 = new MonthlySummaryNotFoundException("trainer2", 2024);
        MonthlySummaryNotFoundException exception3 = new MonthlySummaryNotFoundException("trainer3", 2024, 6);

        ResponseEntity<ApiStandardError> response1 = globalExceptionHandler
                .handleMonthlySummaryNotFoundException(exception1, request);
        ResponseEntity<ApiStandardError> response2 = globalExceptionHandler
                .handleMonthlySummaryNotFoundException(exception2, request);
        ResponseEntity<ApiStandardError> response3 = globalExceptionHandler
                .handleMonthlySummaryNotFoundException(exception3, request);

        assertEquals(HttpStatus.NOT_FOUND, response1.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, response3.getStatusCode());

        ApiStandardError body1 = response1.getBody();
        assertNotNull(body1);
        assertTrue(body1.message().contains("trainer1"));

        ApiStandardError body2 = response2.getBody();
        assertNotNull(body2);
        assertTrue(body2.message().contains("trainer2"));
        assertTrue(body2.message().contains("2024"));

        ApiStandardError body3 = response3.getBody();
        assertNotNull(body3);
        assertTrue(body3.message().contains("trainer3"));
        assertTrue(body3.message().contains("2024/6"));
    }

    @Test
    void shouldHandleInvalidWorkloadDataExceptionWithFieldConstructor() {
        InvalidWorkloadDataException exception = new InvalidWorkloadDataException("duration", "invalid",
                "must be positive");

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleInvalidWorkloadDataException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertTrue(error.message().contains("duration"));
        assertTrue(error.message().contains("invalid"));
        assertTrue(error.message().contains("must be positive"));
    }

    @Test
    void shouldHandleWorkloadProcessingExceptionWithSpecialConstructor() {
        RuntimeException cause = new RuntimeException("Database connection failed");
        WorkloadProcessingException exception = new WorkloadProcessingException("trainer.username", "ADD_TRAINING",
                cause);

        ResponseEntity<ApiStandardError> response = globalExceptionHandler
                .handleWorkloadProcessingException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiStandardError error = response.getBody();
        assertNotNull(error);
        assertTrue(error.message().contains("trainer.username"));
        assertTrue(error.message().contains("ADD_TRAINING"));
    }
}