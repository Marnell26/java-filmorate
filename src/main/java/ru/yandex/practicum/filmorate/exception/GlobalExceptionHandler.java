package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception) {
        List<ValidationError> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();
        log.error("Ошибка валидации");
        return new ErrorResponse("Ошибка валидации", exception.getStatusCode().toString(), errors);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundExceptions(NotFoundException exception) {
        log.error(exception.getMessage());
        return new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND.toString());
    }

}
