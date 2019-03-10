package finalproject.soundcloud.util.exceptions;

public class DoesNotExistException extends SoundCloudException{

    public DoesNotExistException(String object) {
        super("the " + object + " you are trying to use does not exist");
    }
}
