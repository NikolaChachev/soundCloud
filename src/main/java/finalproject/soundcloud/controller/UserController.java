package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.dtos.UserEditDto;
import finalproject.soundcloud.model.dtos.UserLogInDto;
import finalproject.soundcloud.model.dtos.UserLogOutDto;
import finalproject.soundcloud.model.dtos.UserRegisterDto;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.InvalidUserInputException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import finalproject.soundcloud.util.exceptions.UserNotFoundException;
import finalproject.soundcloud.model.repostitories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController extends SessionManagerController{

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDao userDao;

    @PostMapping(value = "/createAccount")
    public String regUser(@RequestBody UserRegisterDto registerDto,HttpSession session) throws SoundCloudException {
            isValidUserRegData(registerDto);
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
        isValidParameters(username,password);
        User user = userRepository.findFirstByUsernameAndPassword(username,password);
        if(user==null){
            throw new UserNotFoundException();
        }
        logUser(session,user);
        return "Welcome , " + user.getUsername();
    }
    @PostMapping(value = "/logout")
    public String logOut(@RequestBody UserLogOutDto logOut, HttpSession session) throws SoundCloudException{
        User user = userRepository.findByUsername(logOut.getUsername());
        System.out.println(user);
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
        return "Successfull update";
    }

    //validate logIn parameters
    private boolean isValidParameters(String username , String password) throws InvalidUserInputException {
        if(!(username.isEmpty() || username.equals("") || username.contains(" ")
                || password.isEmpty() || password.equals("") || password.contains(" "))){
            return true;
        }
        throw new InvalidUserInputException("Username/Password must not be empty");
    }
    //validation methods for user registration
    private boolean isValidUserRegData(UserRegisterDto registerDto) throws InvalidUserInputException{
        String firstPassword= registerDto.getFirstPassword();
        String secondPassword= registerDto.getSecondPassword();
        String email = registerDto.getEmail();
        if(firstPassword.equals(secondPassword)){
            if(isValidPassword(firstPassword) && isValidEmailAddress(email)){
                return true;
            }
        }
        throw new InvalidUserInputException("Passwords must be matching");
    }
    public static boolean isValidEmailAddress(String email) throws InvalidUserInputException {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]" +
                "{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        throw new InvalidUserInputException("Invalid email input.");
    }
    public static boolean isValidPassword(String password) throws InvalidUserInputException {
        String passPatern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})";
        Pattern pattern = Pattern.compile(passPatern);
        Matcher matcher = pattern.matcher(password);
        if(matcher.matches()){
            return true;
        }
        else {
            throw new InvalidUserInputException("Invalid password input.Password must occur at least once:" +
                    "lower case letter,upper case letter and digit.The password length must be more than 8 symbols");
        }
    }
    private void isUserExists(String username,String email) throws InvalidUserInputException{
        User user = userRepository.findByUsernameOrEmail(username,email);
        if(user!=null) {
            throw new InvalidUserInputException("User already exists");
        }
    }
}
