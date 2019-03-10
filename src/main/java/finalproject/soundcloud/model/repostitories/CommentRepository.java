package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Comment findById(long id);
    void removeAllBySongId(long id);
    void removeAllByUserId(long id);
    void removeById(long id);
    List<Comment> getAllBySongIdAndParentCommentId(long songId, long parentId);
    List<Comment> getAllByParentCommentId(long comId);


}
