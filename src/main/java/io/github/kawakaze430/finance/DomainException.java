package io.github.kawakaze430.finance;

import org.springframework.http.HttpStatus;
public class DomainException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    final HttpStatus status;
    final String code;
    public DomainException(HttpStatus status, String code, String message) {
        super(message); this.status = status; this.code = code;
    }
    static DomainException invalid(String message) { return new DomainException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_OPERATION", message); }
    static DomainException conflict(String message) { return new DomainException(HttpStatus.CONFLICT, "CONFLICT", message); }
    static DomainException missing(String resource) { return new DomainException(HttpStatus.NOT_FOUND, "NOT_FOUND", resource + " was not found"); }
}
