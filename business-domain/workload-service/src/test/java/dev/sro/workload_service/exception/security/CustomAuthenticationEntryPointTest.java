package dev.sro.workload_service.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sro.workload_service.util.response.ApiStandardError;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @Mock
    private AuthenticationException authException;

    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @BeforeEach
    void setUp() throws IOException {
        customAuthenticationEntryPoint = new CustomAuthenticationEntryPoint(objectMapper);
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/test");
        lenient().when(response.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    void shouldHandleAuthenticationExceptionWithMessage() throws IOException, ServletException {
        String exceptionMessage = "Invalid JWT token";
        when(authException.getMessage()).thenReturn(exceptionMessage);

        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, capturedError.status());
        assertEquals("Unauthorized", capturedError.error());
        assertEquals(exceptionMessage, capturedError.message());
        assertEquals("/api/v1/test", capturedError.path());
        assertNotNull(capturedError.timestamp());
        assertTrue(capturedError.timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldHandleAuthenticationExceptionWithNullMessage() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn(null);

        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, capturedError.status());
        assertEquals("Unauthorized", capturedError.error());
        assertEquals("Authentication token is missing or invalid", capturedError.message());
        assertEquals("/api/v1/test", capturedError.path());
        assertNotNull(capturedError.timestamp());
    }

    @Test
    void shouldHandleAuthenticationExceptionWithEmptyMessage() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("");

        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals("Authentication token is missing or invalid", capturedError.message());
    }

    @Test
    void shouldHandleAuthenticationExceptionWithBlankMessage() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("   ");

        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals("   ", capturedError.message());
    }

    @Test
    void shouldSetCorrectHttpResponseProperties() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Test message");

        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).getOutputStream();
    }

    @Test
    void shouldWriteJsonToOutputStream() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Test authentication failed");

        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(objectMapper).writeValue(eq(outputStream), any(ApiStandardError.class));
    }

    @Test
    void shouldCreateApiStandardErrorWithCorrectValues() throws IOException, ServletException {
        String testMessage = "JWT token expired";
        String testPath = "/api/v1/protected-endpoint";
        when(authException.getMessage()).thenReturn(testMessage);
        when(request.getRequestURI()).thenReturn(testPath);

        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(401, capturedError.status());
        assertEquals("Unauthorized", capturedError.error());
        assertEquals(testMessage, capturedError.message());
        assertEquals(testPath, capturedError.path());
        assertNotNull(capturedError.timestamp());

        LocalDateTime now = LocalDateTime.now();
        assertTrue(capturedError.timestamp().isBefore(now.plusSeconds(1)));
        assertTrue(capturedError.timestamp().isAfter(now.minusSeconds(1)));
    }

    @Test
    void shouldHandleIOExceptionFromObjectMapper() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Test message");
        doThrow(new IOException("Failed to write JSON")).when(objectMapper).writeValue(any(ServletOutputStream.class),
                any(ApiStandardError.class));

        assertThrows(IOException.class, () -> {
            customAuthenticationEntryPoint.commence(request, response, authException);
        });

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void shouldHandleMultipleConsecutiveCalls() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("First authentication attempt");

        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        when(authException.getMessage()).thenReturn("Second authentication attempt");

        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(response, times(2)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(2)).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper, times(2)).writeValue(eq(outputStream), any(ApiStandardError.class));
    }

    @Test
    void shouldHandleSpecialCharactersInMessage() throws IOException, ServletException {
        String specialMessage = "Autenticación fallida: caracteres especiales ñáéíóú & símbolos @#$%";
        when(authException.getMessage()).thenReturn(specialMessage);

        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(specialMessage, capturedError.message());
    }

    @Test
    void shouldHandleVeryLongMessage() throws IOException, ServletException {
        String longMessage = "Authentication failed: ".repeat(100)
                + "This is a very long authentication error message that should be handled properly";
        when(authException.getMessage()).thenReturn(longMessage);

        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(longMessage, capturedError.message());
    }

    @Test
    void shouldHandleNullRequestURI() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Test message");
        when(request.getRequestURI()).thenReturn(null);

        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertNull(capturedError.path());
    }

    @Test
    void shouldVerifyConstructorDependencyInjection() {
        ObjectMapper testMapper = mock(ObjectMapper.class);

        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint(testMapper);

        assertNotNull(entryPoint);
    }

    @Test
    void shouldVerifyExceptionMessageLogic() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Normal authentication message");
        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor1 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor1.capture());
        assertEquals("Normal authentication message", errorCaptor1.getValue().message());

        reset(objectMapper);
        when(authException.getMessage()).thenReturn(null);
        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor2 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor2.capture());
        assertEquals("Authentication token is missing or invalid", errorCaptor2.getValue().message());

        reset(objectMapper);
        when(authException.getMessage()).thenReturn("");
        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor3 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor3.capture());
        assertEquals("Authentication token is missing or invalid", errorCaptor3.getValue().message());
    }

    @Test
    void shouldHandleCommonAuthenticationScenarios() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("JWT token expired");
        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor1 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor1.capture());
        assertEquals("JWT token expired", errorCaptor1.getValue().message());

        reset(objectMapper);
        when(authException.getMessage()).thenReturn("Invalid credentials");
        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor2 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor2.capture());
        assertEquals("Invalid credentials", errorCaptor2.getValue().message());

        reset(objectMapper);
        when(authException.getMessage()).thenReturn("Account is locked");
        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor3 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor3.capture());
        assertEquals("Account is locked", errorCaptor3.getValue().message());
    }

    @Test
    void shouldHandleIOExceptionFromObjectMapperWriteValue() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Test message");
        doThrow(new IOException("Failed to write JSON to output stream")).when(objectMapper)
                .writeValue(any(ServletOutputStream.class), any(ApiStandardError.class));

        assertThrows(IOException.class, () -> {
            customAuthenticationEntryPoint.commence(request, response, authException);
        });

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void shouldUseCurrentTimestampForEachCall() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Test message");

        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor1 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor1.capture());
        LocalDateTime timestamp1 = errorCaptor1.getValue().timestamp();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        reset(objectMapper);
        customAuthenticationEntryPoint.commence(request, response, authException);

        ArgumentCaptor<ApiStandardError> errorCaptor2 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor2.capture());
        LocalDateTime timestamp2 = errorCaptor2.getValue().timestamp();

        assertTrue(timestamp2.isAfter(timestamp1) || timestamp2.isEqual(timestamp1));
    }

    @Test
    void shouldHandleDifferentRequestPaths() throws IOException, ServletException {
        String[] testPaths = {
                "/api/v1/auth/login",
                "/api/v1/trainers/workload",
                "/api/v1/trainers/monthly-summary",
                "/actuator/health",
                "/swagger-ui.html"
        };

        for (String path : testPaths) {
            when(authException.getMessage()).thenReturn("Authentication required");
            when(request.getRequestURI()).thenReturn(path);

            customAuthenticationEntryPoint.commence(request, response, authException);

            ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
            verify(objectMapper, atLeastOnce()).writeValue(eq(outputStream), errorCaptor.capture());

            ApiStandardError capturedError = errorCaptor.getValue();
            assertEquals(path, capturedError.path());

            reset(objectMapper);
        }
    }
}