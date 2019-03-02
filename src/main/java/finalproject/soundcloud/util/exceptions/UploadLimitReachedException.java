package finalproject.soundcloud.util.exceptions;

public class UploadLimitReachedException extends SoundCloudException {

    public UploadLimitReachedException() {
        super("you have reached your upload time limit! if you wish to upload this song try our premium options!");
    }
}
