package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Comment findById(long id);
    void removeAllByParentCommentId(long id);
    void removeById(long id);
    ArrayList<Comment> getAllByUserId(long id);
    ArrayList<Comment> getAllByParentCommentIdIsAndSongId(long id, long songId);
}
