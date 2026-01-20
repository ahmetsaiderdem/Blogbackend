package com.example.Blogbackend.common;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApi(ApiException e){
        Map<String,Object> body=new HashMap<>();
        body.put("code",e.getCode());
        body.put("message",e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e){
        Map<String,Object> body=new HashMap<>();
        body.put("coder","VALIDATION_ERROR");
        body.put("message","Validation failed");

        Map<String,String> fieldErrors=new HashMap<>();
        for (FieldError fe: e.getBindingResult().getFieldErrors()){
            fieldErrors.put(fe.getField(),fe.getDefaultMessage());
        }
        body.put("fieldErrors",fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e){
        if ("INVALID_CREDENTIALS".equals(e.getMessage())){
            Map<String,Object> body=new HashMap<>();
            body.put("coder","INVALID_CREDENTILAS");
            body.put("message","Email or password is incorrect");
            return ResponseEntity.status(401).body(body);
        }

        if ("EMAIL_ALREADY_EXISTS".equals(e.getMessage())){
            Map<String,Object> body=new HashMap<>();
            body.put("coder","EMAIL_ALREADY_EXISTS");
            body.put("message","Email already exists");
            return ResponseEntity.status(409).body(body);
        }

        Map<String,Object> body=new HashMap<>();
        body.put("coder","INTERNAL_ERROR");
        body.put("message",e.getMessage());
        return ResponseEntity.status(500).body(body);
    }
}
