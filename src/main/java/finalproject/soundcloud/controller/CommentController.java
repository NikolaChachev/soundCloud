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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CommentController extends SessionManagerController {
    @Autowired
    SongRepository songRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentDao commentDao;
    @PostMapping(value = "songs/{id}/comment")
    public ResponseDto addCommentToSong( HttpSession session,@PathVariable("id") long songId,
           @RequestBody CommentDto commentDto) throws SoundCloudException {
        if(commentDto == null){
            throw new InvalidActionException();
        }
        User user = getLoggedUser(session);
        if(songRepository.findById(songId) == null){
            throw new DoesNotExistException("song");
        }
        if(commentDto.getParentId() != 0 && commentRepository.findById(commentDto.getParentId()) == null){
            throw new DoesNotExistException("comment");
        }
        Comment comment = new Comment();
        comment.setParentCommentId(commentDto.getParentId());
        comment.setSongId(songId);
        comment.setUserId(user.getId());
        comment.setLikes(0);
        comment.setSongTime(commentDto.getSongTime());
        comment.setText(commentDto.getText());
        comment.setTime(LocalDateTime.now());
        commentRepository.save(comment);
        return new ResponseDto("comment added!");
    }

    @DeleteMapping(value = "comments/{comId}")
    public ResponseDto removeComment(HttpSession session, @PathVariable("comId") long commentId) throws SoundCloudException{
        User user = getLoggedUser(session);

        if(user.getId() != songRepository.findById(commentRepository.findById(commentId).getSongId()).getUserId()
                || commentRepository.findById(commentId).getUserId() != user.getId()){
            throw new UnauthorizedUserException();
        }
        return commentDao.removeComment(commentId);
    }

    @PutMapping(value = "comments/{id}")
    public ResponseDto rateComment(HttpSession session, @PathVariable("id") long commentId) throws SoundCloudException {
        User user = getLoggedUser(session);
        if(commentRepository.findById(commentId) == null){
            throw new DoesNotExistException("comment");
        }
        return commentDao.rateComment(user.getId(),commentId);
    }

    @GetMapping(value =  "songs/{songId}")
    public List<Comment> getAllParentSongComments(@PathVariable("songId") long songId, HttpSession session) throws DoesNotExistException {
        if(songRepository.findById(songId) == null){
            throw new DoesNotExistException("song");
        }
        return commentRepository.getAllBySongIdAndParentCommentId(songId,0);
    }

    @GetMapping(value = "comments/{commentId}")
    public List<Comment> getAllCommentReplies(@PathVariable("commentId") long commentId,HttpSession session) throws DoesNotExistException {
        if(commentRepository.findById(commentId) == null){
            throw new DoesNotExistException("comment");
        }

        return commentRepository.getAllByParentCommentId(commentId);
    }
}

