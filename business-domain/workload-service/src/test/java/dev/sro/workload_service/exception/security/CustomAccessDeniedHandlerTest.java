package dev.sro.workload_service.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sro.workload_service.util.response.ApiStandardError;
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
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @Mock
    private AccessDeniedException accessDeniedException;

    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @BeforeEach
    void setUp() throws IOException {
        customAccessDeniedHandler = new CustomAccessDeniedHandler(objectMapper);
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/test");
        lenient().when(response.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    void shouldHandleAccessDeniedExceptionWithMessage() throws IOException {
        String exceptionMessage = "User does not have permission to access this resource";
        when(accessDeniedException.getMessage()).thenReturn(exceptionMessage);

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(HttpServletResponse.SC_FORBIDDEN, capturedError.status());
        assertEquals("Forbidden", capturedError.error());
        assertEquals(exceptionMessage, capturedError.message());
        assertEquals("/api/v1/test", capturedError.path());
        assertNotNull(capturedError.timestamp());
        assertTrue(capturedError.timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldHandleAccessDeniedExceptionWithNullMessage() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn(null);

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(HttpServletResponse.SC_FORBIDDEN, capturedError.status());
        assertEquals("Forbidden", capturedError.error());
        assertEquals("Access denied. You don't have the required permissions", capturedError.message());
        assertEquals("/api/v1/test", capturedError.path());
        assertNotNull(capturedError.timestamp());
    }

    @Test
    void shouldHandleAccessDeniedExceptionWithEmptyMessage() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("");

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals("Access denied. You don't have the required permissions", capturedError.message());
    }

    @Test
    void shouldHandleAccessDeniedExceptionWithBlankMessage() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("   ");

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals("   ", capturedError.message());
    }

    @Test
    void shouldSetCorrectHttpResponseProperties() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("Test message");

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).getOutputStream();
    }

    @Test
    void shouldWriteJsonToOutputStream() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("Test access denied");

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        verify(objectMapper).writeValue(eq(outputStream), any(ApiStandardError.class));
    }

    @Test
    void shouldCreateApiStandardErrorWithCorrectValues() throws IOException {
        String testMessage = "Insufficient privileges";
        String testPath = "/api/v1/protected-resource";
        when(accessDeniedException.getMessage()).thenReturn(testMessage);
        when(request.getRequestURI()).thenReturn(testPath);

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(403, capturedError.status());
        assertEquals("Forbidden", capturedError.error());
        assertEquals(testMessage, capturedError.message());
        assertEquals(testPath, capturedError.path());
        assertNotNull(capturedError.timestamp());

        LocalDateTime now = LocalDateTime.now();
        assertTrue(capturedError.timestamp().isBefore(now.plusSeconds(1)));
        assertTrue(capturedError.timestamp().isAfter(now.minusSeconds(1)));
    }

    @Test
    void shouldHandleIOExceptionFromObjectMapper() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("Test message");
        doThrow(new IOException("Failed to write JSON")).when(objectMapper).writeValue(any(ServletOutputStream.class),
                any(ApiStandardError.class));

        assertThrows(IOException.class, () -> {
            customAccessDeniedHandler.handle(request, response, accessDeniedException);
        });

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void shouldHandleMultipleConsecutiveCalls() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("First call");

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        when(accessDeniedException.getMessage()).thenReturn("Second call");

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        verify(response, times(2)).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response, times(2)).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper, times(2)).writeValue(eq(outputStream), any(ApiStandardError.class));
    }

    @Test
    void shouldHandleSpecialCharactersInMessage() throws IOException {
        String specialMessage = "Acceso denegado: caracteres especiales ñáéíóú & símbolos @#$%";
        when(accessDeniedException.getMessage()).thenReturn(specialMessage);

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(specialMessage, capturedError.message());
    }

    @Test
    void shouldHandleVeryLongMessage() throws IOException {
        String longMessage = "Access denied: ".repeat(100)
                + "This is a very long message that should be handled properly";
        when(accessDeniedException.getMessage()).thenReturn(longMessage);

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertEquals(longMessage, capturedError.message());
    }

    @Test
    void shouldHandleNullRequestURI() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("Test message");
        when(request.getRequestURI()).thenReturn(null);

        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());

        ApiStandardError capturedError = errorCaptor.getValue();
        assertNull(capturedError.path());
    }

    @Test
    void shouldVerifyConstructorDependencyInjection() {
        ObjectMapper testMapper = mock(ObjectMapper.class);

        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler(testMapper);

        assertNotNull(handler);
    }

    @Test
    void shouldVerifyExceptionMessageLogic() throws IOException {
        when(accessDeniedException.getMessage()).thenReturn("Normal message");
        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor1 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor1.capture());
        assertEquals("Normal message", errorCaptor1.getValue().message());

        reset(objectMapper);
        when(accessDeniedException.getMessage()).thenReturn(null);
        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor2 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor2.capture());
        assertEquals("Access denied. You don't have the required permissions", errorCaptor2.getValue().message());

        reset(objectMapper);
        when(accessDeniedException.getMessage()).thenReturn("");
        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        ArgumentCaptor<ApiStandardError> errorCaptor3 = ArgumentCaptor.forClass(ApiStandardError.class);
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor3.capture());
        assertEquals("Access denied. You don't have the required permissions", errorCaptor3.getValue().message());
    }
}