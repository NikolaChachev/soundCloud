package finalproject.soundcloud.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class NotLoggedException extends SoundCloudException {
    public NotLoggedException() {
        super("You are not logged");
    }

}
