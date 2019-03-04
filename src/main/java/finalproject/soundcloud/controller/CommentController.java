package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.CommentDao;
import finalproject.soundcloud.model.dtos.CommentDto;
import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Comment;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.CommentRepository;
import finalproject.soundcloud.model.repostitories.SongRepository;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import finalproject.soundcloud.util.exceptions.InvalidActionException;
import finalproject.soundcloud.util.exceptions.NotLoggedException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
public class CommentController extends SessionManagerController {
    @Autowired
    SongRepository songRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentDao commentDao;
    @PostMapping(value = "songs/{id}")
    public ResponseDto addCommentToSong( HttpSession session,
           @RequestBody CommentDto commentDto) throws SoundCloudException {
        isUserLogged(session);
        if(songRepository.findById(commentDto.getSongId()) == null){
            throw new DoesNotExistException("song");
        }
        if(commentDto.getParentId() != 0 && commentRepository.findById(commentDto.getParentId()) == null){
            throw new DoesNotExistException("comment");
        }
        Comment comment = new Comment();
        comment.setParentCommentId(commentDto.getParentId());
        comment.setSongId(commentDto.getSongId());
        comment.setUserId(commentDto.getUserId());
        comment.setLikes(0);
        comment.setSongTime(commentDto.getSongTime());
        comment.setText(commentDto.getText());
        comment.setTime(LocalDateTime.now());
        commentRepository.save(comment);
        return new ResponseDto("comment added!");
    }
    @PostMapping(value = "songs/{id}/comments/{comId}")
    public ResponseDto removeComment(HttpSession session, @RequestParam("comId") long commentId) throws SoundCloudException{
        isUserLogged(session);
        User user = (User) session.getAttribute(LOGGED);
        return commentDao.removeComment(user.getId(),commentId);
    }
    
}

