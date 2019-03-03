package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.repostitories.CommentRepository;
import finalproject.soundcloud.util.exceptions.InvalidActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CommentRepository commentRepository;
    public ResponseDto removeComment(long userId,long commentId) throws InvalidActionException {
        if(commentRepository.findById(commentId).getUserId() != userId){
            throw new InvalidActionException();
        }
        commentRepository.removeAllByParentCommentId(commentRepository.findById(commentId).getParentCommentId());
        commentRepository.removeById(commentId);
        return new ResponseDto("comment removed !");
    }
}
