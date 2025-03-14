package backend.academy.bot.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorResponseTest {

    @Test
    void shouldCreateApiErrorResponseWithValidData() {
        // Arrange
        List<String> stacktrace = List.of("line1", "line2", "line3");

        // Act
        ApiErrorResponse response = new ApiErrorResponse(
            "Error description",
            "404",
            "NotFoundException",
            "Resource not found",
            stacktrace
        );

        // Assert
        assertThat(response.getDescription()).isEqualTo("Error description");
        assertThat(response.getCode()).isEqualTo("404");
        assertThat(response.getExceptionName()).isEqualTo("NotFoundException");
        assertThat(response.getExceptionMessage()).isEqualTo("Resource not found");
        assertThat(response.getStacktrace()).containsExactly("line1", "line2", "line3");
    }

    @Test
    void shouldHandleEmptyStacktrace() {
        // Arrange
        List<String> emptyStacktrace = List.of();

        // Act
        ApiErrorResponse response = new ApiErrorResponse(
            "Error description",
            "500",
            "InternalServerErrorException",
            "Something went wrong",
            emptyStacktrace
        );

        // Assert
        assertThat(response.getStacktrace()).isEmpty();
    }

    @Test
    void shouldHandleNullStacktrace() {
        // Arrange
        List<String> nullStacktrace = null;

        // Act
        ApiErrorResponse response = new ApiErrorResponse(
            "Error description",
            "400",
            "BadRequestException",
            "Invalid request",
            nullStacktrace
        );

        // Assert
        assertThat(response.getStacktrace()).isNull();
    }
}
