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
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import finalproject.soundcloud.util.exceptions.UnauthorizedUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        if(commentDto == null){
            throw new InvalidActionException();
        }
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
    public ResponseDto removeComment(HttpSession session, @PathVariable("comId") long commentId,@PathVariable("id") long songId) throws SoundCloudException{
        isUserLogged(session);
        User user = (User) session.getAttribute(LOGGED);

        if(user.getId() != songRepository.findById(songId).getUserId()
                || commentRepository.findById(commentId).getUserId() != user.getId()){
            throw new UnauthorizedUserException();
        }
        return commentDao.removeComment(commentId);
    }
    @PostMapping(value = "comments/{id}")
    public ResponseDto rateComment(HttpSession session, @PathVariable("id") long commentId) throws SoundCloudException {
        isUserLogged(session);
        if(commentRepository.findById(commentId) == null){
            throw new DoesNotExistException("comment");
        }
        User user = (User) session.getAttribute(LOGGED);
        return commentDao.rateComment(user.getId(),commentId);
    }
}

