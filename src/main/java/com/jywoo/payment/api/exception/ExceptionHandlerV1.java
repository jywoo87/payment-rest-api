package com.jywoo.payment.api.exception;

import com.jywoo.payment.api.exception.dto.ErrorDto;
import com.jywoo.payment.except.*;
import com.jywoo.payment.except.message.ExceptionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "com.jywoo.payment.api")
@Slf4j
public class ExceptionHandlerV1 {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AbstractRuntimeException.class)
    @ResponseBody
    public ErrorDto handleRuntimeException(HttpServletRequest req, AbstractRuntimeException ex) {
        log.error("", ex);

        final String requestUrl = req.getRequestURL().toString();
        return ErrorDto.builder()
                .requestUrl(requestUrl)
                .code(ex.getExceptionMessage().getCode())
                .errors(Arrays.asList(ex.getExceptionMessage().getMessage()))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ErrorDto handleConstraintVioaltionException(HttpServletRequest req, ConstraintViolationException ex) {
        log.error("", ex);

        final String requestUrl = req.getRequestURL().toString();
        return ErrorDto.builder()
                .requestUrl(requestUrl)
                .code(ExceptionMessage.BAD_REQUEST.getCode())
                .errors(ex.getConstraintViolations().stream().map(t -> t.getMessage()).collect(Collectors.toList()))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TransactionSystemException.class)
    @ResponseBody
    public ErrorDto handleTransactionSystemException(HttpServletRequest req, TransactionSystemException ex) {
        log.error("", ex);

        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) cause).getConstraintViolations();

            final String requestUrl = req.getRequestURL().toString();
            return ErrorDto.builder()
                    .requestUrl(requestUrl)
                    .code(ExceptionMessage.BAD_REQUEST.getCode())
                    .errors(constraintViolations.stream().map(t -> t.getMessage()).collect(Collectors.toList()))
                    .build();
        }

        return handleDefaultException(req, ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorDto handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException ex) {
        log.error("", ex);

        final String requestUrl = req.getRequestURL().toString();
        return ErrorDto.builder()
                .requestUrl(requestUrl)
                .code(ExceptionMessage.BAD_REQUEST.getCode())
                .errors(ex.getBindingResult().getAllErrors().stream().map(t-> t.getDefaultMessage()).collect(Collectors.toList()))
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseBody
    public ErrorDto handle(HttpServletRequest req, TransactionNotFoundException ex) {
        log.error("", ex);

        final String requestUrl = req.getRequestURL().toString();
        return ErrorDto.builder()
                .requestUrl(requestUrl)
                .code(ex.getExceptionMessage().getCode())
                .errors(Arrays.asList(ex.getMessage()))
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({RemainVatException.class, NotEnoughVatException.class, NotEnoughPriceException.class})
    @ResponseBody
    public ErrorDto handle(HttpServletRequest req, AbstractRuntimeException ex) {
        log.error("", ex);

        final String requestUrl = req.getRequestURL().toString();
        return ErrorDto.builder()
                .requestUrl(requestUrl)
                .code(ex.getExceptionMessage().getCode())
                .errors(Arrays.asList(ex.getMessage()))
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorDto handleDefaultException(HttpServletRequest req, Exception ex) {
        log.error("", ex);

        final String requestUrl = req.getRequestURL().toString();
        return ErrorDto.builder()
                .requestUrl(requestUrl)
                .code(ExceptionMessage.INTERNAL_SERVICE_ERROR.getCode())
                .errors(Arrays.asList(ex.getMessage()))
                .build();
    }

}
