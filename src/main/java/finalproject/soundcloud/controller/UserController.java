package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.daos.UserValidationDao;
import finalproject.soundcloud.model.dtos.ResponseDto;
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
import java.util.ArrayList;

@RestController
public class UserController extends SessionManagerController{

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDao userDao;
    @Autowired
    ResponseDto responseDto;

    @PostMapping(value = "/createAccount")
    public ResponseDto regUser(@RequestBody UserRegisterDto registerDto,HttpSession session) throws SoundCloudException {
        UserValidationDao.validateUserRegData(registerDto);
            isUserExists(registerDto.getUsername(),registerDto.getEmail());
            User user = new User();
            user.setUsername(registerDto.getUsername());
            user.setPassword(registerDto.getFirstPassword());
            user.setEmail(registerDto.getEmail());
            user.setPro(Boolean.parseBoolean(registerDto.getIsPro()));
            user.setProfilePicture(registerDto.getPicturePath());
            userRepository.save(user);
            logUser(session,user);
            responseDto.setResponse("Your registration was successfull");
            return responseDto;

    }
    @PostMapping(value = "/signin")
    public ResponseDto signIn(@RequestBody UserLogInDto logDto,HttpSession session) throws SoundCloudException {
        String username = logDto.getUsername();
        String password = logDto.getPassword().trim();
        UserValidationDao.validateLogInParameters(username,password);
        User user = userRepository.findFirstByUsernameAndPassword(username,password);
        if(user==null){
            throw new UserNotFoundException();
        }
        logUser(session,user);
        responseDto.setResponse( "Welcome , " + user.getUsername());
        return responseDto;
    }
    @PostMapping(value = "/logout")
    public ResponseDto logOut(HttpSession session) throws SoundCloudException{
        User user = (User)session.getAttribute(LOGGED);
        logOutUser(session);
        responseDto.setResponse("You logged out successfully " + user.getUsername() + " .See you soon!");
        return responseDto;
    }
    @PostMapping(value = "/edit")
    public ResponseDto editProfile(@RequestBody UserEditDto editDto,HttpSession session) throws SoundCloudException{
        isUserLogged(session);
        User user = (User)session.getAttribute(LOGGED);
        long userId = user.getId();
        userDao.updateUser(editDto,user,userId);
        responseDto.setResponse("Successfull update!");
        return responseDto;
    }
    @DeleteMapping(value = "/deleteProfile")
    public ResponseDto deleteProfile(HttpSession session) throws SoundCloudException{
        isUserLogged(session);
        User user = (User)session.getAttribute(LOGGED);
        userRepository.delete(user);
        responseDto.setResponse("Your profile has been deleted.We hope to seee you soon!");
        return responseDto;
    }

    @GetMapping(value = "/getAllUser")
    public ArrayList<User> getAll(HttpSession session) throws SoundCloudException{

        return userRepository.findAllUserByUsername("do");
    }

    public void isUserExists(String username,String email) throws InvalidUserInputException{
        User user = userRepository.findByUsernameOrEmail(username,email);
        if(user!=null) {
            throw new InvalidUserInputException("User already exists");
        }
    }

}
