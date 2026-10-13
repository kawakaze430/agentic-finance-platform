package io.github.kawakaze430.finance;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(DomainException.class)
    ResponseEntity<ProblemDetail> domain(DomainException e,HttpServletRequest r) { return problem(e.status,e.code,e.getMessage(),r); }
    @ExceptionHandler({DataIntegrityViolationException.class,ObjectOptimisticLockingFailureException.class})
    ResponseEntity<ProblemDetail> conflict(Exception e,HttpServletRequest r) { return problem(HttpStatus.CONFLICT,"CONFLICT","A concurrent or duplicate operation conflicts with stored data; retry with the original idempotency key",r); }
    @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,org.springframework.web.bind.MissingRequestHeaderException.class,org.springframework.web.bind.MissingServletRequestParameterException.class})
    ResponseEntity<ProblemDetail> invalid(Exception e,HttpServletRequest r) { return problem(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","Check required fields, formats, and request headers",r); }
    private ResponseEntity<ProblemDetail> problem(HttpStatus status,String code,String detail,HttpServletRequest r) {
        var p=ProblemDetail.forStatusAndDetail(status,detail); p.setTitle(code); p.setProperty("code",code); p.setProperty("requestId",r.getAttribute("requestId"));
        return ResponseEntity.status(status).body(p);
    }
}
