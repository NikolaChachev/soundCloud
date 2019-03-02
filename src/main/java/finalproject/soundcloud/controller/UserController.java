package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.SongDao;
import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.daos.UserValidationDao;
import finalproject.soundcloud.model.dtos.*;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.InvalidUserInputException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import finalproject.soundcloud.util.exceptions.UserNotFoundException;
import finalproject.soundcloud.model.repostitories.UserRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;

@RestController
public class UserController extends SessionManagerController{

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDao userDao;
    @Autowired
    SongDao songDao;
    @Autowired
    ResponseDto responseDto;

    public static final String IMAGE_DIR = "D:\\ITtalents\\FinalProject\\pictures\\";
    public static final String SONGS_DIR = "D:\\ITtalents\\FinalProject\\songs\\";


    @PostMapping(value = "/createAccount")
    public ResponseDto regUser(@RequestBody UserRegisterDto registerDto,HttpSession session) throws SoundCloudException {
        UserValidationDao.validateUserRegData(registerDto);
            isUserExists(registerDto.getUsername(),registerDto.getEmail());
            User user = new User();
            user.setUsername(registerDto.getUsername());
            user.setPassword(registerDto.getFirstPassword());
            user.setEmail(registerDto.getEmail());
            user.setPro(Boolean.parseBoolean(registerDto.getIsPro()));
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
    @PutMapping(value = "users/edit/{id}")
    public ResponseDto editProfile(@RequestBody UserEditDto editDto, @PathVariable("id") long userParamId,
                                   HttpSession session) throws SoundCloudException{
        isUserLogged(session);
        User user = (User)session.getAttribute(LOGGED);
        long userId = user.getId();
        if(userId == userParamId) {
            userDao.updateUser(editDto, user, userId);
            responseDto.setResponse("Successfull update!");
            return responseDto;
        }
        throw new InvalidUserInputException("You are UNAUTHORIZED to edit this profile.");
    }
    @DeleteMapping(value = "users/deleteProfile/{id}")
    public ResponseDto deleteProfile(@PathVariable("id") long userParamId, HttpSession session) throws SoundCloudException{
        isUserLogged(session);
        User user = (User)session.getAttribute(LOGGED);
        long userId = user.getId();
        if(userId == userParamId) {
            //todo delete all users's files
            userRepository.delete(user);
            responseDto.setResponse("Your profile has been deleted.We hope to seee you soon!");
            logOut(session);
            return responseDto;
        }
        throw new InvalidUserInputException("You are UNAUTHORIZED to delete this profile.");
    }
    @PostMapping("users/{id}/uploadImages")
    public ResponseDto uploadImage(@RequestBody ImageDto dto,@PathVariable ("id") long id, HttpSession session) throws Exception {
        User user = (User) session.getAttribute(LOGGED);
        isUserLogged(session);
        if(user.getId() == id) {
            String base64 = dto.getPicturePath();
            byte[] bytes = Base64.getDecoder().decode(base64);
            String name = user.getId() + System.currentTimeMillis() + ".png";
            File newImage = new File(IMAGE_DIR + name);
            FileOutputStream fos = new FileOutputStream(newImage);
            fos.write(bytes);
            user.setProfilePicture(name);
            userRepository.save(user);
            responseDto.setResponse("Image uploaded successfully");
            return responseDto;
        }
        else {
            throw new InvalidUserInputException("You are UNAUTHORIZED to upload a picure at this profile.");
        }
    }
    @GetMapping(value="users/{id}/profileImage", produces = "image/png")
    public byte[] downloadImage(@PathVariable("id") long id, HttpSession session) throws Exception {
        User user = (User) session.getAttribute(LOGGED);
        isUserLogged(session);
        if(user.getId()==id) {
            File newImage = new File(IMAGE_DIR + user.getProfilePicture());
            UserValidationDao.hasUserProfilePicture(newImage);
            FileInputStream fis = new FileInputStream(newImage);
            return IOUtils.toByteArray(fis);
        }
        throw new InvalidUserInputException("You are UNAUTHORIZED to see the picure at this profile.");
    }
    @DeleteMapping("users/{id}/deleteProfileImage")
    public ResponseDto deleteImage(@PathVariable ("id") long id, HttpSession session) throws Exception {
        User user = (User) session.getAttribute(LOGGED);
        isUserLogged(session);
        if(user.getId() == id) {
            File newImage = new File(IMAGE_DIR + user.getProfilePicture());
            UserValidationDao.hasUserProfilePicture(newImage);
            newImage.delete();
            user.setProfilePicture(null);
            responseDto.setResponse("Image deleted successfully");
            return responseDto;
        }
        else {
            throw new InvalidUserInputException("You are UNAUTHORIZED to delete a picure at this profile.");
        }
    }


    @PostMapping("users/{id}/uploadSongs")

    public ResponseDto uploadImage(@RequestBody SongDto dto, @PathVariable("id") long id, HttpSession session) throws Exception {
        User user = (User) session.getAttribute(LOGGED);
        isUserLogged(session);
        if(user.getId() == id) {
            String base64 = dto.getSongFilePath();
            byte[] bytes = Base64.getDecoder().decode(base64);
            String name = user.getId() + System.currentTimeMillis() + ".mp3";
            File newImage = new File(SONGS_DIR + name);
            FileOutputStream fos = new FileOutputStream(newImage);
            fos.write(bytes);
            //todo
            //songDao.uploadSong(fos,user);
            responseDto.setResponse("Songs uploaded successfully");
            return responseDto;
        }
        else {
            throw new InvalidUserInputException("You are UNAUTHORIZED to upload a songs at this profile.");
        }
    }
    @DeleteMapping(value="users/{id}/songs/{song_name}")
    public ResponseDto deleteSong(@PathVariable("id") long id,@PathVariable String song_name, HttpSession session) throws Exception {
        User user = (User) session.getAttribute(LOGGED);
        isUserLogged(session);
        if(user.getId() == id) {
            File song = new File(SONGS_DIR + song_name);
            UserValidationDao.hasUserSongs(song);
            // todo
            // userDao.geleteSong(song);
            responseDto.setResponse("Song deleted successfully");
            return responseDto;
        }
        throw new InvalidUserInputException("You are UNAUTHORIZED to delete a song at this profile.");
    }




    private void isUserExists(String username,String email) throws InvalidUserInputException{
        User user = userRepository.findByUsernameOrEmail(username,email);
        if(user!=null) {
            throw new InvalidUserInputException("User already exists");
        }
    }

}
