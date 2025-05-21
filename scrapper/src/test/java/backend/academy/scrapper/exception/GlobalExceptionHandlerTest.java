package backend.academy.scrapper.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.model.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturn400WhenConstraintViolationExceptionIsThrown() {
        // Arrange
        String exceptionMessage = "Invalid input parameters";
        ConstraintViolationException exception = new ConstraintViolationException(exceptionMessage, null);

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.error400(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiErrorResponse body = response.getBody();
        assert body != null;
        assertEquals("Некорректные параметры запроса", body.description());
        assertEquals("400", body.code());
        assertFalse(body.stacktrace().isEmpty());
        assertTrue(body.exceptionName().contains("ConstraintViolationException"));
        assertEquals(exceptionMessage, body.exceptionMessage());
    }

    @Test
    void shouldReturn404WhenIllegalArgumentExceptionIsThrown() {
        // Arrange
        String exceptionMessage = "Invalid parameter provided";
        IllegalArgumentException exception = new IllegalArgumentException(exceptionMessage);

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.error404(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiErrorResponse body = response.getBody();
        assert body != null;
        assertEquals("Ресурс не существует", body.description());
        assertEquals("404", body.code());
        assertFalse(body.stacktrace().isEmpty());
        assertTrue(body.exceptionName().contains("IllegalArgumentException"));
        assertEquals(exceptionMessage, body.exceptionMessage());
    }
}
