package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.SongDao;
import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.daos.UserValidationDao;
import finalproject.soundcloud.model.dtos.*;
import finalproject.soundcloud.model.dtos.searchDtos.UserSearchDto;
import finalproject.soundcloud.model.pojos.Song;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.SongRepository;
import finalproject.soundcloud.model.repostitories.UserRepository;
import finalproject.soundcloud.util.AmazonClient;
import finalproject.soundcloud.util.BCryptUtil;
import finalproject.soundcloud.util.MailUtil;
import finalproject.soundcloud.util.exceptions.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;


@RestController
public class UserController extends SessionManagerController{

    @Autowired
    UserRepository userRepository;
    @Autowired
    SongRepository songRepository;
    @Autowired
    UserDao userDao;
    @Autowired
    SongDao songDao;
    @Autowired
    ResponseDto responseDto;

    public static final String IMAGE_DIR = "D:\\AApictures\\";
    static Logger logger = Logger.getLogger(UserController.class.getName());

    @PostMapping(value = "/createAccount")
    public User regUser(@RequestBody UserRegisterDto registerDto,HttpSession session) throws Exception {
        if(registerDto==null){
            throw new SoundCloudException("Empty input");
        }
        UserValidationDao.validateUserRegData(registerDto);
        isUserExists(registerDto.getUsername(),registerDto.getEmail());
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(BCryptUtil.hashPassword(registerDto.getFirstPassword()));
        user.setEmail(registerDto.getEmail());
        user.setUserType(registerDto.getUserType());
        String autoGenerateKey = getRandomString();
        user.setActivationKey(autoGenerateKey);
        userRepository.save(user);
        new Thread(()->{
            try {
                MailUtil.sendMail("Confirm your registration",
                        "Click here http://localhost:8090/activateAccount?activation_key="+autoGenerateKey,
                        user.getEmail());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }).start();
        return userRepository.findByUsername(user.getUsername());
    }
    @GetMapping(value = "/activateAccount")
    public ResponseDto activateUserAccount(@RequestParam(value = "activation_key") String activationKey, HttpSession session) throws SoundCloudException {
        User user = userRepository.findByActivationKey(activationKey);
        System.out.println(user);
        if(user.getIs_active() == 1){
            responseDto.setResponse("Your profile is already activated");
            return responseDto;
        }
        user.setIs_active(1);
        userRepository.save(user);
        responseDto.setResponse("Registration verified");
        return responseDto;
    }
    @PostMapping(value = "/signin")
    public User signIn(@RequestBody UserLogInDto logDto,HttpSession session) throws SoundCloudException {
        if(logDto == null){
            throw new SoundCloudException("Empty input");
        }
        String username = logDto.getUsername();
        String password = logDto.getPassword().trim();
        UserValidationDao.validateLogInParameters(username,password);
        User user = userRepository.findByUsername(username);
        if(user==null || !BCryptUtil.checkPass(password,user.getPassword())){
            throw new UserNotFoundException();
        }
        if(user.getIs_active()==0){
            throw new InvalidUserInputException("Please enter the activation key first");
        }
        logUser(session,user);
        return user;
    }
    @PostMapping(value = "/logout")
    public ResponseDto logOut(HttpSession session) throws Exception{
        User user = (User)session.getAttribute(LOGGED);
        logOutUser(session);
        responseDto.setResponse("You logged out successfully " + user.getUsername() + " .See you soon!");
        return responseDto;
    }
    @PostMapping(value = "/forgotPassword")
    public ResponseDto forgotPassword(@RequestParam ("email") String email)throws UserNotFoundException{
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new UserNotFoundException();
        }
        new Thread(()->{
            try {
                MailUtil.sendMail("Reset passwod","Open this link to reset your password:" +
                        " http://localhost:8090/password_reset/"+user.getId(),user.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        responseDto.setResponse("Email send successfully.Now you can reset your password");
        return responseDto;
    }
    @PostMapping(value = "/password_reset/{user_id}")
    public ResponseDto resetPassword(@PathVariable("user_id") long id, @RequestBody UserRegisterDto resetPass,HttpSession session) throws SoundCloudException{
        String fPassword = resetPass.getFirstPassword();
        String sPassword = resetPass.getSecondPassword();
        if(UserValidationDao.validatePassword(fPassword) && fPassword.equals(sPassword)){
            User user = userRepository.findById(id);
            user.setPassword(BCryptUtil.hashPassword(fPassword));
            userRepository.save(user);
            logUser(session,user);
            responseDto.setResponse("Your password was reset");
            return responseDto;

        }
        throw new InvalidUserInputException("Passwords MUST be equals");

    }
    @PutMapping(value = "/users/{id}/edit")
    public User editProfile(@RequestBody UserEditDto editDto, @PathVariable("id") long userParamId,
                                   HttpSession session) throws Exception{
        if(editDto == null){
            throw new SoundCloudException("Empty input");
        }
        User user = getLoggedUser(session);
        long userId = user.getId();
        if(userId == userParamId) {
            userDao.updateUser(editDto, user, userId);
            return userRepository.findById(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @DeleteMapping(value = "/users/{id}/deleteProfile")
    public User deleteProfile(@PathVariable("id") long userParamId, HttpSession session)
            throws Exception{
        User user = getLoggedUser(session);
        long userId = user.getId();
        if(userId == userParamId) {
            userRepository.delete(user);
            return user;
        }
        throw new UnauthorizedUserException();
    }
    @PostMapping("/users/{id}/uploadImages")
    public ResponseDto uploadImage(@RequestPart(value = "file") MultipartFile file, @PathVariable ("id") long id, HttpSession session)
            throws Exception {
        User user = getLoggedUser(session);
        if(user.getId() == id) {
            if(user.getProfilePicture() != null){
                File image = new File(IMAGE_DIR + user.getProfilePicture());
                image.delete();
            }
            String name = user.getId() + System.currentTimeMillis() + ".png";
            File newImage = new File(IMAGE_DIR + name);
            file.transferTo(newImage);
            user.setProfilePicture(name);
            userRepository.save(user);
            responseDto.setResponse("Image uploaded successfully");
            return responseDto;
        }
        else {
            throw new UnauthorizedUserException();
        }
    }
    @GetMapping(value="/users/{id}/profileImage", produces = "image/png")
    public byte[] downloadImage(@PathVariable("id") long id, HttpSession session) throws Exception {
        User user = getLoggedUser(session);
        if(user.getId()==id) {
            File newImage = new File(IMAGE_DIR + user.getProfilePicture());
            UserValidationDao.hasUserProfilePicture(newImage);
            FileInputStream fis = new FileInputStream(newImage);
            return IOUtils.toByteArray(fis);
        }
        throw new UnauthorizedUserException();
    }
    @DeleteMapping("/users/{id}/deleteProfileImage")
    public ResponseDto deleteImage(@PathVariable ("id") long id, HttpSession session) throws Exception {
        User user = getLoggedUser(session);
        if(user.getId() == id) {
            File image = new File(IMAGE_DIR + user.getProfilePicture());
            UserValidationDao.hasUserProfilePicture(image);
            image.delete();
            user.setProfilePicture(null);
            responseDto.setResponse("Image deleted successfully");
            return responseDto;
        }
        throw new UnauthorizedUserException();
    }
    @PostMapping("users/addSong")
    public Song addSong(@RequestBody SongDto songDto, HttpSession session) throws SoundCloudException{
        User user = getLoggedUser(session);
            String name = songDto.getSongName();
            isSongExists(name,user.getId());
            boolean isPublic = songDto.isPublic();
            Song song = new Song();
            song.setSongName(name);
            song.setPublic(isPublic);
            song.setUserId(user.getId());
            songRepository.save(song);
            return songRepository.findBySongNameAndUserId(name,user.getId());
    }
    @PostMapping("/{song_id}/uploadSongs")
    public Song uploadSong(@RequestPart(value = "file") MultipartFile file, @PathVariable("song_id") long id, HttpSession session) throws Exception {
        User user = getLoggedUser(session);
        Song song = songRepository.findById(id);
        if(song == null){
            throw new InvalidUserInputException("Song not found");

        }
        if(user.getId() == song.getUserId()) {
            if(song.getFilePath()!=null){
                throw new InvalidUserInputException("Already uploaded");
            }
            File songFile = AmazonClient.convertMultiPartToFile(file);
            if(!songDao.canUploadSong(user,songFile)){
                songRepository.deleteById(id);
                throw new UploadLimitReachedException();
            }
            String filePath = user.getId() + "_" + song.getSongName();
            songDao.uploadSong(filePath,songFile,id,song.getSongName());
            new Thread(()->{
                try {
                    notifyFollowers(user,song);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }).start();
            return songRepository.findById(id);
        }
        throw new UnauthorizedUserException();
    }
    @DeleteMapping(value= "users/{user_id}/songs/{song_id}")
    public Song deleteSong(@PathVariable("user_id") long userId,@PathVariable("song_id") long songId, HttpSession session)
            throws Exception {
        User user = getLoggedUser(session);
        if(user.getId() == userId) {
            Song song = songRepository.findById(songId);
            if(song==null){
                throw new InvalidUserInputException("Song not found");
            }
            songDao.deleteSong(song);

            return song;
        }
        throw new UnauthorizedUserException();
    }
    @PostMapping("users/{id}/follow/{f_id}")
    public User followUser(@PathVariable("id") long id , @PathVariable("f_id") long f_id, HttpSession session)
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
            return following;
        }
        throw new UnauthorizedUserException();
    }
    @PostMapping("users/{id}/unfollow/{f_id}")
    public User unfollowUser(@PathVariable("id") long id , @PathVariable("f_id") long f_id, HttpSession session)
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
            return following;
        }
        throw new UnauthorizedUserException();
    }


    private void isUserExists(String username,String email) throws InvalidUserInputException{
        User user = userRepository.findByUsernameOrEmail(username,email);
        if(user!=null) {
            throw new InvalidUserInputException("User already exists");
        }
    }

    private Song isSongExists(String songName,long userId) throws InvalidUserInputException{
        Song song = songRepository.findBySongNameAndUserId(songName,userId);
        if(song!=null) {
            throw new InvalidUserInputException("Song already exists");
        }
        return song;
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

    private void notifyFollowers(User user, Song song) throws Exception {
        for (UserSearchDto u : userDao.getAllFollowers(user.getId())) {
            User follower = userRepository.findByUsername(u.getUsername());
            MailUtil.sendMail("ADDED NEW SONG", user.getUsername() + " added new song: " +
                    song.getSongName() + ". Maybe you will like it." , follower.getEmail());
        }
    }


}
