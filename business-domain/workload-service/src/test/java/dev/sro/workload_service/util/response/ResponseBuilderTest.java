package dev.sro.workload_service.util.response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseBuilderTest {

    private final String testPath = "http://localhost/test-path";

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test-path");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testSuccessWithData() {
        String testData = "test data";
        ResponseEntity<ApiStandardResponse<String>> responseEntity = ResponseBuilder.success(testData);
        ApiStandardResponse<String> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), responseBody.status()),
                () -> assertEquals("Operation completed successfully", responseBody.message()),
                () -> assertEquals(testPath, responseBody.path()),
                () -> assertEquals(testData, responseBody.data()));
    }

    @Test
    void testSuccessWithStatusMessageAndData() {
        String testData = "test data";
        String message = "Custom success message";
        HttpStatus status = HttpStatus.ACCEPTED;

        ResponseEntity<ApiStandardResponse<String>> responseEntity = ResponseBuilder.success(status, message, testData);
        ApiStandardResponse<String> responseBody = responseEntity.getBody();

        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(status.value(), responseBody.status()),
                () -> assertEquals(message, responseBody.message()),
                () -> assertEquals(testPath, responseBody.path()),
                () -> assertEquals(testData, responseBody.data()));
    }

    @Test
    void testCreated() {
        String testData = "new resource";
        ResponseEntity<ApiStandardResponse<String>> responseEntity = ResponseBuilder.created(testData);
        ApiStandardResponse<String> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), responseBody.status()),
                () -> assertEquals("Resource created successfully", responseBody.message()),
                () -> assertEquals(testPath, responseBody.path()),
                () -> assertEquals(testData, responseBody.data()));
    }

    @Test
    void testNoContent() {
        ResponseEntity<ApiStandardResponse<Void>> responseEntity = ResponseBuilder.noContent();
        ApiStandardResponse<Void> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), responseBody.status()),
                () -> assertEquals("No content", responseBody.message()),
                () -> assertEquals(testPath, responseBody.path()),
                () -> assertNull(responseBody.data()));
    }

    @Test
    void testList() {
        List<String> testData = List.of("item1", "item2");
        ResponseEntity<ApiStandardResponse<List<String>>> responseEntity = ResponseBuilder.list(testData);
        ApiStandardResponse<List<String>> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), responseBody.status()),
                () -> assertEquals("List retrieved successfully", responseBody.message()),
                () -> assertEquals(testPath, responseBody.path()),
                () -> assertEquals(testData, responseBody.data()));
    }

    @Test
    void testWorkloadProcessed() {
        String testData = "workload data";
        ResponseEntity<ApiStandardResponse<String>> responseEntity = ResponseBuilder.workloadProcessed(testData);
        ApiStandardResponse<String> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), responseBody.status()),
                () -> assertEquals("Workload processed successfully", responseBody.message()),
                () -> assertEquals(testPath, responseBody.path()),
                () -> assertEquals(testData, responseBody.data()));
    }

    @Test
    void testSummaryRetrieved() {
        String testData = "summary data";
        ResponseEntity<ApiStandardResponse<String>> responseEntity = ResponseBuilder.summaryRetrieved(testData);
        ApiStandardResponse<String> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), responseBody.status()),
                () -> assertEquals("Summary retrieved successfully", responseBody.message()),
                () -> assertEquals(testPath, responseBody.path()),
                () -> assertEquals(testData, responseBody.data()));
    }
}