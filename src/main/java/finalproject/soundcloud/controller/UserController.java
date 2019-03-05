package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.SongDao;
import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.daos.UserValidationDao;
import finalproject.soundcloud.model.dtos.*;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.UserRepository;
import finalproject.soundcloud.util.MailUtil;
import finalproject.soundcloud.util.exceptions.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Random;


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
    public ResponseDto regUser(@RequestBody UserRegisterDto registerDto,HttpSession session) throws Exception {
        if(registerDto==null){
            throw new SoundCloudException("Empty input");
        }
        UserValidationDao.validateUserRegData(registerDto);
        isUserExists(registerDto.getUsername(),registerDto.getEmail());
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getFirstPassword());
        user.setEmail(registerDto.getEmail());
        user.setUserType(Integer.parseInt(registerDto.getUserType()));
        String autoGenerateKey = getRandomString();
        user.setActivation_key(autoGenerateKey);
        userRepository.save(user);
        new Thread(()->{
            try {
                MailUtil.sendMail("Activation code : "+autoGenerateKey  ,user.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        responseDto.setResponse("Your registration was successfull." +
                "Now enter the activation code you received on your email");
        return responseDto;
    }
    @PostMapping(value = "/activateAccount")
    public ResponseDto activateUserAccaount(@RequestBody UserActivationDto actDto, HttpSession session) throws SoundCloudException {
        if(actDto == null){
            throw new SoundCloudException("Empty input");
        }
        String username = actDto.getUsername();
        String password = actDto.getPassword().trim();
        String activationCode = actDto.getActivation_key();
        User user = userRepository.findFirstByUsernameAndPassword(username,password);
        if(user==null){
            throw new UserNotFoundException();
        }
        if(user.getIs_active() == 1){
            responseDto.setResponse("Your profile is already activated");
            return responseDto;
        }
        if(user.getActivation_key().equals(activationCode)){
            user.setIs_active(1);
            userRepository.save(user);
            logUser(session,user);
            responseDto.setResponse("Registration verified");
            return responseDto;
        }
        throw new InvalidUserInputException("Active key isn't valid. Try again");
    }
    @PostMapping(value = "/signin")
    public ResponseDto signIn(@RequestBody UserLogInDto logDto,HttpSession session) throws SoundCloudException {
        if(logDto == null){
            throw new SoundCloudException("Empty input");
        }
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
    public ResponseDto logOut(HttpSession session) throws Exception{
        User user = (User)session.getAttribute(LOGGED);
        logOutUser(session);
        responseDto.setResponse("You logged out successfully " + user.getUsername() + " .See you soon!");
        return responseDto;
    }
    @PutMapping(value = "users/{id}/edit")
    public ResponseDto editProfile(@RequestBody UserEditDto editDto, @PathVariable("id") long userParamId,
                                   HttpSession session) throws Exception{
        if(editDto == null){
            throw new SoundCloudException("Empty input");
        }
        User user = getLoggedUser(session);
        long userId = user.getId();
        if(userId == userParamId) {
            userDao.updateUser(editDto, user, userId);
            responseDto.setResponse("Successfull update!");
            return responseDto;
        }
        throw new InvalidUserInputException("You are UNAUTHORIZED to edit this profile.");
    }
    @DeleteMapping(value = "users/{id}/deleteProfile")
    public ResponseDto deleteProfile(@PathVariable("id") long userParamId, HttpSession session)
            throws Exception{
        User user = getLoggedUser(session);
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
    public ResponseDto uploadImage(@RequestBody ImageDto dto,@PathVariable ("id") long id, HttpSession session)
            throws Exception {
        if(dto == null){
            throw new SoundCloudException("Empty input");
        }
        User user = getLoggedUser(session);
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
        User user = getLoggedUser(session);
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
        User user = getLoggedUser(session);
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
    public ResponseDto uploadSong(@RequestBody SongDto dto, @PathVariable("id") long id, HttpSession session)
            throws Exception {
        if(dto == null){
            throw new SoundCloudException("Empty input");
        }
        User user = getLoggedUser(session);
        if(user.getId() == id) {
            String base64 = dto.getSongFilePath();
            byte[] bytes = Base64.getDecoder().decode(base64);
            String name = user.getId() + System.currentTimeMillis() +".mp3";
            File song = new File(SONGS_DIR + name);
            FileOutputStream fos = new FileOutputStream(song);
            if(!songDao.canUploadSong(user,song)){
                throw new UploadLimitReachedException();
            }
            System.out.println("Predi zapisvaneto");
            fos.write(bytes);
            System.out.println("Sled zapisvaneto" + song);
            songDao.uploadSong(name,user,dto,song);
            responseDto.setResponse("Songs uploaded successfully");
            return responseDto;
        }
        else {
            throw new InvalidUserInputException("You are UNAUTHORIZED to upload a songs at this profile.");
        }
    }
    @DeleteMapping(value="users/{id}/songs/{song_name}")
    public ResponseDto deleteSong(@PathVariable("id") long id,@PathVariable String song_name, HttpSession session)
            throws Exception {
        User user = getLoggedUser(session);
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
    @PostMapping("users/{id}/follow/{f_id}")
    public ResponseDto followUser(@PathVariable("id") long id , @PathVariable("f_id") long f_id, HttpSession session)
            throws Exception{
        User user = getLoggedUser(session);
        User following = userRepository.findById(f_id);
        if(following == null){
            throw new UserNotFoundException();
        }
        if(user.getId() == id){
            if(userDao.checkForFollowing(user,following)){
                throw new InvalidUserInputException("You are already following this user!");
            }
            userDao.follow(user,following);
            responseDto.setResponse("You started following " + following.getUsername());
            return responseDto;
        }
        throw new UnauthorizedUserException();
    }
    @PostMapping("users/{id}/unfollow/{f_id}")
    public ResponseDto unfollowUser(@PathVariable("id") long id , @PathVariable("f_id") long f_id, HttpSession session)
            throws Exception {
        User user = getLoggedUser(session);
        User following = userRepository.findById(f_id);
        if(following == null){
            throw new UserNotFoundException();
        }
        if(user.getId() == id){
            if(!userDao.checkForFollowing(user,following)){
                throw new InvalidUserInputException("You don't follow this user!");
            }
            userDao.unfollow(user,following);
            responseDto.setResponse("You stop following " + following.getUsername());
            return responseDto;
        }
        throw new UnauthorizedUserException();
    }


    private void isUserExists(String username,String email) throws InvalidUserInputException{
        User user = userRepository.findByUsernameOrEmail(username,email);
        if(user!=null) {
            throw new InvalidUserInputException("User already exists");
        }
    }

    protected String getRandomString() {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < 12) { // length of the random string.
            int index = (int) (rnd.nextFloat() * symbols.length());
            str.append(symbols.charAt(index));
        }
        return str.toString();

    }

}
