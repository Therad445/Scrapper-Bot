package backend.academy.scrapper.exception;

import backend.academy.scrapper.model.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> error400(ConstraintViolationException exception) {
        String exceptionName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();
        List<String> stacktrace = getStackTraceAsList(exception);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Некорректные параметры запроса", // Статичное описание
            "400", // Статичный код
            exceptionName,
            exceptionMessage,
            stacktrace
        );
        log.error("Ошибка 400: {}", apiErrorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(apiErrorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> error404(IllegalArgumentException exception) {
        String exceptionName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();
        List<String> stacktrace = getStackTraceAsList(exception);

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Request parameter is invalid", // Статичное описание
            "404", // Статичный код
            exceptionName,
            exceptionMessage,
            stacktrace
        );

        log.error("Ошибка 404: {}", apiErrorResponse);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(apiErrorResponse);
    }

    private List<String> getStackTraceAsList(Exception exception) {
        return Arrays.stream(exception.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.toList());
    }
}
