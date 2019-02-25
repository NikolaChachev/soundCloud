package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.User;
import finalproject.soundcloud.model.exceptions.InvalidUserInputException;
import finalproject.soundcloud.model.repostitories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "/createAccount")
    public void regUser(@RequestBody User user) throws InvalidUserInputException {
        try {
            System.out.println(user);
            isValidEmailAddress(user.getEmail());
            isValidPassword(user.getPassword());
            userRepository.save(user);
        }catch (InvalidUserInputException e){
            throw new InvalidUserInputException(e.getMessage());
        }
    }


    public boolean isValidEmailAddress(String email) throws InvalidUserInputException {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        throw new InvalidUserInputException("Invalid email input.");
    }
    public boolean isValidPassword(String password) throws InvalidUserInputException {
        String passPatern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})";
        Pattern pattern = Pattern.compile(passPatern);
        Matcher matcher = pattern.matcher(password);
        if(matcher.matches()){
            return true;
        }
        else {
            throw new InvalidUserInputException("Invalid password input.Password must occur at least once:" +
                    "lower case letter,upper case letter and digit.The password length must be more then 8 symbols");
        }
    }
}
