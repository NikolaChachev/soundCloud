package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.SongDao;
import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.dtos.SongEditDto;
import finalproject.soundcloud.model.pojos.Song;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.SongRepository;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import finalproject.soundcloud.util.exceptions.InvalidUserInputException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class SongController extends SessionManagerController{
    @Autowired
    SongRepository songRepository;
    @Autowired
    ResponseDto responseDto;
    @Autowired
    SongDao songDao;


    @PutMapping(value = "/users/{id}/songs")
    public ResponseDto rateSong(@RequestBody SongEditDto songId, HttpSession session, @RequestParam("q") boolean q) throws Exception{
        if(songId == null){
            throw new DoesNotExistException("bad request!");
        }
        User user = getLoggedUser(session);
        return songDao.rateSong(songId.getSongId(),user,q);
    }

    @PutMapping(value = "/songs/{id}/repost")
    public ResponseDto repostSong(HttpSession session, @PathVariable long id)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        Song song = songRepository.findById(id);
        if(song == null){
            throw new DoesNotExistException("song");
        }
        if(songDao.hasItPosted(user,id)){
            throw new InvalidUserInputException("you can not repost a song you have already reposted");
        }
        return songDao.repostSong(user,id);
    }
    @PutMapping(value = "/songs/{id}/unpost")
    public ResponseDto unpostSong(HttpSession session, @PathVariable long id)
            throws SoundCloudException {
        User user = getLoggedUser(session);
        Song song = songRepository.findById(id);
        if(song == null){
            throw new DoesNotExistException("song");
        }
        if(!songDao.hasItPosted(user,id)){
            throw new InvalidUserInputException("you can not unpost a song you have not posted");
        }
        return songDao.unpostSong(user,id);
    }



}
