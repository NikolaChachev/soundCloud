package finalproject.soundcloud.util.exceptions;

public class UnauthorizedUserException extends SoundCloudException {

    public UnauthorizedUserException() {
        super("You are unauthorized for this action");
    }
}
