package web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class PreconditionFailed extends RuntimeException{
    public PreconditionFailed(String message){
        super(message);
    }
}
