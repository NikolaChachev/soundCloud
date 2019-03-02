package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.pojos.ErrorMessage;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import finalproject.soundcloud.util.exceptions.NotLoggedException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import finalproject.soundcloud.util.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;


public abstract class SessionManagerController {
    public static final String LOGGED = "logged";

    @ExceptionHandler({DoesNotExistException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleNotFound(Exception e){
        return new ErrorMessage(e.getMessage(),HttpStatus.BAD_REQUEST.value(),LocalDateTime.now());
    }
    @ExceptionHandler({NotLoggedException.class, UserNotFoundException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleNotLogged(Exception e){
        ErrorMessage msg = new ErrorMessage(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
        return msg;
    }

    @ExceptionHandler({SoundCloudException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMyErrors(Exception e){
        ErrorMessage msg = new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return msg;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleOtherErrors(Exception e){
        ErrorMessage msg = new ErrorMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        return msg;
    }

    //Session check
    public static boolean isUserLogged(HttpSession session) throws NotLoggedException {
        if(session.isNew() || session.getAttribute(LOGGED) == null) {
            throw new NotLoggedException();
        }
        return true;
    }

    public static void logUser(HttpSession session, User user){
        session.setAttribute(LOGGED,user);
    }

    public static void logOutUser(HttpSession session) throws NotLoggedException{
        if(isUserLogged(session)) {
            session.setAttribute(LOGGED, null);
            return;
        }
        throw new NotLoggedException();
    }

}
