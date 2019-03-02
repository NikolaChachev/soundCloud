package finalproject.soundcloud.util.exceptions;

public class DoesNotExistException extends SoundCloudException{

    public DoesNotExistException(String object) {
        super("the " + object + " does not exist");
    }
}
