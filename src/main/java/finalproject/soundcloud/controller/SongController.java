package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.SongDao;
import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.dtos.SongEditDto;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.SongRepository;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
@RestController
public class SongController extends SessionManagerController{
    @Autowired
    SongRepository songRepository;
    @Autowired
    ResponseDto responseDto;
    @Autowired
    SongDao songDao;


    @PostMapping(value = "/users/{id}/songs")
    public ResponseDto likeSong(@RequestBody SongEditDto songId, HttpSession session) throws Exception{
        if(songId == null){
            throw new DoesNotExistException("bad request! bitch");
        }
        User user = (User) session.getAttribute(LOGGED);
        isUserLogged(session);
        return songDao.likeSong(songId.getSongId(),user);
    }
    @PostMapping(value = "songs")
    public ResponseDto dislikeSong(){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResponse("test successful");
        return responseDto;
    }
}
