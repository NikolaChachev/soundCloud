package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.UserRegisterDto;
import finalproject.soundcloud.util.exceptions.InvalidUserInputException;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidationDao {

    private UserValidationDao(){};

    //validate logIn parameters
    public static boolean validateLogInParameters(String username , String password) throws InvalidUserInputException {
        if(!(username.isEmpty() || username.equals("") || username.contains(" ")
                || password.isEmpty() || password.equals("") || password.contains(" "))){
            return true;
        }
        throw new InvalidUserInputException("Username/Password must not be empty");
    }
    //validation methods for user registration
    public static boolean validateUserRegData(UserRegisterDto registerDto) throws InvalidUserInputException{
        String username = registerDto.getUsername();
        String firstPassword= registerDto.getFirstPassword();
        String secondPassword= registerDto.getSecondPassword();
        String email = registerDto.getEmail();
        if(firstPassword.equals(secondPassword)){
            if(validatePassword(firstPassword) && validateEmailAddress(email) && validateUsername(username)!=null ){
                return true;
            }
        }
        throw new InvalidUserInputException("Passwords must be matching");
    }
    public static boolean validateEmailAddress(String email) throws InvalidUserInputException {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]" +
                "{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        throw new InvalidUserInputException("Invalid email input.");
    }
    public static boolean validatePassword(String password) throws InvalidUserInputException {
        String passPatern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})";
        Pattern pattern = Pattern.compile(passPatern);
        Matcher matcher = pattern.matcher(password);
        if(matcher.matches() && !password.contains(" ")){
            return true;
        }
        else {
            throw new InvalidUserInputException("Invalid password input.Password must occur at least once:" +
                    "lower case letter,upper case letter and digit.The password length must be more than 8 symbols");
        }
    }
    public static String validateUsername(String username) throws InvalidUserInputException{
        if(username.equals("")){
            return null;
        }
        if(!username.matches("^[a-zA-Z0-9_-]{3,15}") || username.contains(" ")){
            throw new InvalidUserInputException("Username mustn't contains white space!");
        }
        return username;
    }

    //validate firstName,lastName,city,country
    public static String validateOtherData(String str) throws InvalidUserInputException{
        if(str.equals("")){
            return null;
        }
        if(!str.matches("[a-zA-Z]+") || str.contains(" ")){
            throw new InvalidUserInputException("The field with parameter : '" + str + "' isn't valid." +
                    " Please enter a valid data!");
        }
        return str;
    }

    //has the user got a profile picture
    public static boolean hasUserProfilePicture(File file) throws InvalidUserInputException{
        if(file.exists()){
            return true;
        }
        throw new InvalidUserInputException("You haven't got a profile picture!");
    }


}
