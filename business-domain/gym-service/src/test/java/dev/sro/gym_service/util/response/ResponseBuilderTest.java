package dev.sro.gym_service.util.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    private static final String MOCK_PATH = "/api/v1/test";
    private ServletUriComponentsBuilder mockBuilder;
    private UriComponents mockUriComponents;

    @BeforeEach
    void setUp() {
        mockBuilder = mock(ServletUriComponentsBuilder.class);
        mockUriComponents = mock(UriComponents.class);
    }

    @Test
    void success_WithDataOnly_ShouldReturnSuccessResponseWith200Status() {
        String testData = "test data";
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<String>> response = ResponseBuilder.success(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<String> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("Operation completed successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
            assertNotNull(body.timestamp());
        }
    }

    @Test
    void success_WithNullData_ShouldReturnSuccessResponseWithNullData() {
        String testData = null;
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<String>> response = ResponseBuilder.success(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<String> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("Operation completed successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertNull(body.data());
        }
    }

    @Test
    void success_WithComplexObject_ShouldReturnSuccessResponseWithComplexData() {
        TestObject testData = new TestObject("test", 123);
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<TestObject>> response = ResponseBuilder.success(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<TestObject> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("Operation completed successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"CREATED", "ACCEPTED", "NO_CONTENT", "UNAUTHORIZED", "FORBIDDEN", "NOT_FOUND", "INTERNAL_SERVER_ERROR"})
    void success_WithCustomStatusAndMessage_ShouldReturnResponseWithCorrectStatusAndMessage(String statusName) {
        HttpStatus status = HttpStatus.valueOf(statusName);
        String customMessage = "Custom message for " + statusName;
        String testData = "test data";
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<String>> response = ResponseBuilder.success(status, customMessage, testData);

            assertNotNull(response);
            assertEquals(status, response.getStatusCode());
            
            ApiStandardResponse<String> body = response.getBody();
            assertNotNull(body);
            assertEquals(status.value(), body.status());
            assertEquals(customMessage, body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
        }
    }

    @Test
    void success_WithCustomStatusMessageAndNullData_ShouldReturnResponseWithNullData() {    
        HttpStatus status = HttpStatus.ACCEPTED;
        String customMessage = "Custom message";
        String testData = null;
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<String>> response = ResponseBuilder.success(status, customMessage, testData);

            assertNotNull(response);
            assertEquals(status, response.getStatusCode());
            
            ApiStandardResponse<String> body = response.getBody();
            assertNotNull(body);
            assertEquals(status.value(), body.status());
            assertEquals(customMessage, body.message());
            assertEquals(MOCK_PATH, body.path());
            assertNull(body.data());
        }
    }

    @Test
    void created_WithData_ShouldReturnCreatedResponseWith201Status() {
        String testData = "created data";
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<String>> response = ResponseBuilder.created(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            
            ApiStandardResponse<String> body = response.getBody();
            assertNotNull(body);
            assertEquals(201, body.status());
            assertEquals("Resource created successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
        }
    }

    @Test
    void created_WithNullData_ShouldReturnCreatedResponseWithNullData() {
        String testData = null;
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<String>> response = ResponseBuilder.created(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            
            ApiStandardResponse<String> body = response.getBody();
            assertNotNull(body);
            assertEquals(201, body.status());
            assertEquals("Resource created successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertNull(body.data());
        }
    }

    @Test
    void created_WithComplexObject_ShouldReturnCreatedResponseWithComplexData() {
        TestObject testData = new TestObject("created", 456);
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<TestObject>> response = ResponseBuilder.created(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            
            ApiStandardResponse<TestObject> body = response.getBody();
            assertNotNull(body);
            assertEquals(201, body.status());
            assertEquals("Resource created successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
        }
    }

    @Test
    void noContent_ShouldReturnNoContentResponseWith204Status() {
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<Void>> response = ResponseBuilder.noContent();

            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            
            ApiStandardResponse<Void> body = response.getBody();
            assertNotNull(body);
            assertEquals(204, body.status());
            assertEquals("No content", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertNull(body.data());
        }
    }

    @Test
    void list_WithPopulatedList_ShouldReturnListResponseWith200Status() {
        List<String> testData = Arrays.asList("item1", "item2", "item3");
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<List<String>>> response = ResponseBuilder.list(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<List<String>> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("List retrieved successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
            assertEquals(3, body.data().size());
        }
    }

    @Test
    void list_WithEmptyList_ShouldReturnListResponseWithEmptyList() {
        List<String> testData = Collections.emptyList();
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<List<String>>> response = ResponseBuilder.list(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<List<String>> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("List retrieved successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
            assertTrue(body.data().isEmpty());
        }
    }

    @Test
    void list_WithNullList_ShouldReturnListResponseWithNullData() {
        List<String> testData = null;
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<List<String>>> response = ResponseBuilder.list(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<List<String>> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("List retrieved successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertNull(body.data());
        }
    }

    @Test
    void list_WithComplexObjectList_ShouldReturnListResponseWithComplexObjects() {
        List<TestObject> testData = Arrays.asList(
            new TestObject("object1", 1),
            new TestObject("object2", 2)
        );
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<List<TestObject>>> response = ResponseBuilder.list(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<List<TestObject>> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("List retrieved successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
            assertEquals(2, body.data().size());
        }
    }

    @Test
    void list_WithSingleElementList_ShouldReturnListResponseWithSingleElement() {
        List<String> testData = Arrays.asList("single item");
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<List<String>>> response = ResponseBuilder.list(testData);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            ApiStandardResponse<List<String>> body = response.getBody();
            assertNotNull(body);
            assertEquals(200, body.status());
            assertEquals("List retrieved successfully", body.message());
            assertEquals(MOCK_PATH, body.path());
            assertEquals(testData, body.data());
            assertEquals(1, body.data().size());
            assertEquals("single item", body.data().get(0));
        }
    }

    @Test
    void allMethods_ShouldSetTimestampCorrectly() {
        String testData = "test";
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(MOCK_PATH);

            ResponseEntity<ApiStandardResponse<String>> successResponse = ResponseBuilder.success(testData);
            ResponseEntity<ApiStandardResponse<String>> createdResponse = ResponseBuilder.created(testData);
            ResponseEntity<ApiStandardResponse<Void>> noContentResponse = ResponseBuilder.noContent();
            ResponseEntity<ApiStandardResponse<List<String>>> listResponse = ResponseBuilder.list(Arrays.asList(testData));

            assertNotNull(successResponse.getBody().timestamp());
            assertNotNull(createdResponse.getBody().timestamp());
            assertNotNull(noContentResponse.getBody().timestamp());
            assertNotNull(listResponse.getBody().timestamp());
        }
    }

    @Test
    void allMethods_ShouldUseCurrentRequestPath() {
        String customPath = "/api/v1/custom/path";
        String testData = "test";
        
        try (MockedStatic<ServletUriComponentsBuilder> mockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            mockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockUriComponents);
            when(mockUriComponents.toUriString()).thenReturn(customPath);

            ResponseEntity<ApiStandardResponse<String>> successResponse = ResponseBuilder.success(testData);
            ResponseEntity<ApiStandardResponse<String>> createdResponse = ResponseBuilder.created(testData);
            ResponseEntity<ApiStandardResponse<Void>> noContentResponse = ResponseBuilder.noContent();
            ResponseEntity<ApiStandardResponse<List<String>>> listResponse = ResponseBuilder.list(Arrays.asList(testData));

            assertEquals(customPath, successResponse.getBody().path());
            assertEquals(customPath, createdResponse.getBody().path());
            assertEquals(customPath, noContentResponse.getBody().path());
            assertEquals(customPath, listResponse.getBody().path());
        }
    }

    private static class TestObject {
        private final String name;
        private final int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return value == that.value && java.util.Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, value);
        }
    }
} 