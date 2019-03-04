package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Comment;
import finalproject.soundcloud.model.repostitories.CommentRepository;
import finalproject.soundcloud.util.exceptions.InvalidActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
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
    public ResponseDto rateComment(long userId, long commentId) {
        String sql = "SELECT * FROM users_liked_comments WHERE user_id = ? AND comment_id = ?";
        RowCountCallbackHandler callbackHandler = new RowCountCallbackHandler();
         jdbcTemplate.query(sql,new Object[]{userId,commentId},callbackHandler);
         int rowCount = callbackHandler.getRowCount();
        if(rowCount != 0){
            sql = "DELETE  FROM users_liked_comments WHERE user_id = ? AND comment_id = ?";
            Comment comment = commentRepository.findById(commentId);
            comment.setLikes(comment.getLikes() - 1);
            commentRepository.save(comment);
        }else{
            Comment comment = commentRepository.findById(commentId);
            comment.setLikes(comment.getLikes() + 1);
            commentRepository.save(comment);
            sql = "INSERT INTO users_liked_comments(user_id,comment_id) VALUES(?,?)";
        }
        jdbcTemplate.update(sql,userId,commentId);
        return new ResponseDto("action complete!");
    }
}
