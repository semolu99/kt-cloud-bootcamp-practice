package com.example.my_api_server.config;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.BindException;
import java.security.SignatureException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BaseResponse<String>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().getFirst();
        String message = fieldError.getField().replaceFirst("_", "") + ": " + fieldError.getDefaultMessage();
        return ResponseEntity
                .status(ResultCode.BAD_REQUEST.getStatusCode())
                .body(new BaseResponse<>(ResultCode.BAD_REQUEST.getStatusCode(), LocalDateTime.now(), ResultCode.BAD_REQUEST.getMessage(), null));
    }

    /// 매개변수 값이 올바르게 처리 되지 않았을때 에러처리
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<BaseResponse<String>> illegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(ResultCode.BAD_REQUEST.getStatusCode())
                .body(new BaseResponse<>(ResultCode.BAD_REQUEST.getStatusCode(), LocalDateTime.now(), ResultCode.BAD_REQUEST.getMessage(), null));
    }

//    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
//    protected fun httpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException, req: HttpServletRequest): ResponseEntity<BaseResponse<String>> {
//        return ResponseEntity(BaseResponse(statusCode = ResultCode.REST_TYPE_ERROR.statusCode, statusMessage = "Does not support request method '" + req.method + "'", code = ResultCode.REST_TYPE_ERROR.code), HttpStatus.INTERNAL_SERVER_ERROR)
//    }
//
//    @ExceptionHandler(InvalidInputException::class)
//    protected fun apiCustomException(ex: InvalidInputException): ResponseEntity<BaseResponse<String>> {
//        return ResponseEntity(BaseResponse(statusCode = ex.statusCode, statusMessage = ex.statusMessage, code = ex.code), HttpStatus.BAD_REQUEST)
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleException(Exception ex) {

        ResultCode resultCode = switch (ex) {
            case NoHandlerFoundException e -> ResultCode.NOT_FOUND;
            case BindException e -> ResultCode.INVALID_DATA;
            case HttpMessageNotReadableException e -> ResultCode.INVALID_JSON;
            case SignatureException e -> ResultCode.INVALID_ACCESS_TOKEN;
            case SecurityException x /*MalformedJwtException t*/ -> ResultCode.INVALID_ACCESS_TOKEN;
            case RuntimeException e -> ResultCode.RUN_TIME_ERROR;
            default -> ResultCode.INTERNAL_SERVER_ERROR;
        };

        BaseResponse<String> response = new BaseResponse<>(
                resultCode.getStatusCode(),
                LocalDateTime.now(),
                resultCode.getMessage(),
                null
        );

        return ResponseEntity
                .status(resultCode.getStatusCode())
                .body(response);
    }

}
