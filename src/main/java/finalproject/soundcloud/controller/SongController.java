package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.SongDao;
import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Song;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.SongRepository;
import finalproject.soundcloud.model.repostitories.UserRepository;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import finalproject.soundcloud.util.exceptions.InvalidUserInputException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class SongController extends SessionManagerController{
    @Autowired
    SongRepository songRepository;
    @Autowired
    ResponseDto responseDto;
    @Autowired
    SongDao songDao;
    @Autowired
    UserRepository userRepository;
    @PutMapping(value = "/songs/{songId}")
    public Song rateSong(@PathVariable("songId") long songId, HttpSession session, @RequestParam("like") boolean isLike) throws Exception{
        if(songRepository.findById(songId) == null){
            throw new DoesNotExistException("song");
        }
        User user = getLoggedUser(session);
        songDao.rateSong(songId,user,isLike);
        return songRepository.findById(songId);
    }

    @GetMapping(value = "songs/{userId}")
    public List<Song> getAllSongsByUser(@PathVariable("userId") long userId,HttpSession session) throws SoundCloudException {
        if(userRepository.findById(userId) == null){
            throw new DoesNotExistException("user");
        }
        return songRepository.findAllByUserId(userId);
    }
    @PutMapping(value = "/songs/{id}/repost")
    public ResponseDto repostSong(HttpSession session, @PathVariable long id)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(songRepository.findById(id) == null){
            throw new DoesNotExistException("song");
        }
        if(songDao.hasItPosted(user,id)){
            throw new InvalidUserInputException("you can not repost a song you have already reposted");
        }
        return songDao.repostSong(user,id);
    }
    @DeleteMapping(value = "/songs/{id}/unpost")
    public ResponseDto unpostSong(HttpSession session, @PathVariable("id") long songId)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        if(songRepository.findById(songId) == null){
            throw new DoesNotExistException("song");
        }
        if(!songDao.hasItPosted(user,songId)){
            throw new InvalidUserInputException("you can not unpost a song you have not posted");
        }
        return songDao.unpostSong(user,songId);
    }



}
