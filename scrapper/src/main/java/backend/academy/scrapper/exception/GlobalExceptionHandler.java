package backend.academy.scrapper.exception;

import backend.academy.scrapper.model.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                "Некорректные параметры запроса", "400", exceptionName, exceptionMessage, stacktrace);
        log.error("Ошибка 400: {}", apiErrorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> error404(IllegalArgumentException exception) {
        String exceptionName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();
        List<String> stacktrace = getStackTraceAsList(exception);

        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse("Ресурс не существует", "404", exceptionName, exceptionMessage, stacktrace);

        log.error("Ошибка 404: {}", apiErrorResponse);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    private List<String> getStackTraceAsList(Exception exception) {
        return Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> unreadable(HttpMessageNotReadableException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                "Некорректный URL",
                "400",
                ex.getClass().getSimpleName(),
                ex.getMostSpecificCause().getMessage(),
                getStackTraceAsList(ex));

        log.error("Ошибка 400: {}", body);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> error400(MethodArgumentNotValidException ex) {

        String errMsg = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .findFirst()
                .orElse("Некорректные параметры запроса");

        ApiErrorResponse body = new ApiErrorResponse(
                "Некорректный URL", "400", ex.getClass().getSimpleName(), errMsg, getStackTraceAsList(ex));

        log.error("Ошибка 400: {}", body);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
