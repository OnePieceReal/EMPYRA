package net.study.springboot.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.UNPROCESSABLE_ENTITY)
public class DataFormatException extends RuntimeException{
    public DataFormatException(String message){
        super(message);
    }
}
