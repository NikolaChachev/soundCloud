package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    public Comment findById(long id);
    public void removeAllByParentCommentId(long id);
    public void removeById(long id);
}
