package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.dtos.searchDtos.*;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import finalproject.soundcloud.util.exceptions.UnauthorizedUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserInformationController extends SessionManagerController {

    @Autowired
    UserDao userDao;

    @GetMapping(value = "/users/{id}/uploadedSongs")
    public List<SongSearchDto> uploadedSongs(@PathVariable("id") long id, HttpSession session)
            throws Exception {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getMyUploadedSongs(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @GetMapping(value = "/users/{id}/uploadedPlaylists")
    public List<PlaylistsSearchDto> showMyUploadedPlaylists(@PathVariable("id") long id, HttpSession session)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getMyUploadedPlaylists(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @GetMapping(value = "/users/{id}/reposts")
    public List<UserSongDto> getMyReposts(@PathVariable("id") long id, HttpSession session)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getMyReposts(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @GetMapping(value = "/users/{id}/likedSongs")
    public List<UserSongDto> likedSongs(@PathVariable("id") long id, HttpSession session)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getMyLikedSongs(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @GetMapping(value = "/users/{id}/likedPlaylists")
    public List<PlaylistsSearchDto> likedPlaylists(@PathVariable("id") long id, HttpSession session)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getMyLikedPlaylists(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @GetMapping(value = "/users/{id}/followers")
    public List<UserSearchDto> followers(@PathVariable("id") long id, HttpSession session)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getAllFollowers(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @GetMapping(value = "/users/{id}/following")
    public List<UserSearchDto> following(@PathVariable("id") long id, HttpSession session)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getAllFollowing(user.getId());
        }
        throw new UnauthorizedUserException();
    }
    @GetMapping(value = "/users/{id}/history")
    public List<HistorySearchDto> history(@PathVariable("id") long id, HttpSession session)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(user.getId() == id){
            return userDao.getHistory(user.getId());
        }
        throw new UnauthorizedUserException();
    }

}
