package finalproject.soundcloud.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidUserInputException extends SoundCloudException {
    @Override
    public String getMessage() {
        return super.getMessage();
    }
    public InvalidUserInputException(String message) {
        super(message);
    }
}
