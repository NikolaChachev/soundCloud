package finalproject.soundcloud.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidUserInputException extends Exception {
    @Override
    public String getMessage() {
        return super.getMessage();
    }
    public InvalidUserInputException(String message) {
        super(message);
    }
}
