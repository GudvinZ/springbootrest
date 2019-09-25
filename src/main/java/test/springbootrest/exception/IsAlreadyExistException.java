package test.springbootrest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IsAlreadyExistException extends RuntimeException {
    public IsAlreadyExistException(String message) {
        super(message);
    }

    public IsAlreadyExistException() {
    }
}