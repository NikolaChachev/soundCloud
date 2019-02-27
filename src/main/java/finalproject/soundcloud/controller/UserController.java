package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.daos.UserValidationDao;
import finalproject.soundcloud.model.dtos.UserEditDto;
import finalproject.soundcloud.model.dtos.UserLogInDto;
import finalproject.soundcloud.model.dtos.UserRegisterDto;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.InvalidUserInputException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import finalproject.soundcloud.util.exceptions.UserNotFoundException;
import finalproject.soundcloud.model.repostitories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
public class UserController extends SessionManagerController{

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDao userDao;

    @PostMapping(value = "/createAccount")
    public String regUser(@RequestBody UserRegisterDto registerDto,HttpSession session) throws SoundCloudException {
        UserValidationDao.validateUserRegData(registerDto);
            isUserExists(registerDto.getUsername(),registerDto.getEmail());
            User user = new User();
            user.setUsername(registerDto.getUsername());
            user.setPassword(registerDto.getFirstPassword());
            user.setEmail(registerDto.getEmail());
            user.setPro(registerDto.isPro());
            user.setProfilePicture(registerDto.getPicturePath());
            userRepository.save(user);
            logUser(session,user);
            return "Your registration was successfull";

    }
    @PostMapping(value = "/signin")
    public String signIn(@RequestBody UserLogInDto logDto,HttpSession session) throws SoundCloudException {
        String username = logDto.getUsername();
        String password = logDto.getPassword().trim();
        UserValidationDao.validateLogInParameters(username,password);
        User user = userRepository.findFirstByUsernameAndPassword(username,password);
        if(user==null){
            throw new UserNotFoundException();
        }
        logUser(session,user);
        return "Welcome , " + user.getUsername();
    }
    @PostMapping(value = "/logout")
    public String logOut(HttpSession session) throws SoundCloudException{
        User user = (User)session.getAttribute(LOGGED);
        logOutUser(session);
        return "You logged out successfully " + user.getUsername() + " .See you soon!";
    }
    @PostMapping(value = "/edit")
    public String editProfile(@RequestBody UserEditDto editDto,HttpSession session) throws SoundCloudException{
        isUserLogged(session);
        User user = (User)session.getAttribute(LOGGED);
        System.out.println(user);
        long userId = user.getId();
        userDao.updateUser(editDto,user,userId);
        return "Successfull update!";
    }
    @PostMapping(value = "/deleteProfile")
    public String deleteProfile(HttpSession session) throws SoundCloudException{
        isUserLogged(session);
        User user = (User)session.getAttribute(LOGGED);
        userRepository.delete(user);
        return "Your profile has been deleted.We hope to seee you soon!";
    }

    public void isUserExists(String username,String email) throws InvalidUserInputException{
        User user = userRepository.findByUsernameOrEmail(username,email);
        if(user!=null) {
            throw new InvalidUserInputException("User already exists");
        }
    }

}
