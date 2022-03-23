package com.example.managedevices.exception.handler;

import com.example.managedevices.exception.BadRequestException;
import com.example.managedevices.exception.ConflictException;
import com.example.managedevices.exception.NotFoundException;
import com.example.managedevices.exception.ServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

@RestControllerAdvice
public class EmsExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(Exception e){
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity handleConflictException(Exception e){
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequestException(Exception e){
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity handleServerException(Exception e){
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
