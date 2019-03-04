package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Comment;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CommentDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CommentRepository commentRepository;

    public ResponseDto removeComment(long commentId)  {
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

    public boolean removeAllUserComments(User user)  {
        ArrayList<Comment> comments = commentRepository.getAllByUserId(user.getId());
        for (Comment c : comments){
            removeComment(c.getId());
        }
        return true;
    }

    public boolean removeAllCommentsFromSong(long songId)  {
        ArrayList<Comment> parents = commentRepository.getAllByParentCommentIdIsAndSongId(0,songId);
        for (Comment c : parents){
            removeComment(c.getId());
        }
        return true;
    }

}
