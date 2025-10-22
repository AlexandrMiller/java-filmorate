package ru.yandex.practicum.filmorate.exceptionHandler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeptions.IllegalStatemantException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exeptions.ValidException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidArgumentException(NotFoundException exception) {
        HashMap<String,Object> responce = new HashMap<>();
        responce.put("errorMessage",exception.getMessage());
        responce.put("errorCode", HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responce);
    }

    @ExceptionHandler(ValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidException(ValidException exception) {
        HashMap<String,Object> responce = new HashMap<>();
        responce.put("errorMessage",exception.getMessage());
        responce.put("errorCode", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responce);
    }

    @ExceptionHandler(IllegalStatemantException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalException(IllegalStatemantException exception) {
        HashMap<String,Object> responce = new HashMap<>();
        responce.put("errorMessage",exception.getMessage());
        responce.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responce);
    }
}
