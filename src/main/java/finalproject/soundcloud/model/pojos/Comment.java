package finalproject.soundcloud.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Column(name = "comment_id")
    private long id;
    @Column(name = "song_id")
    private long songId;
    @Column(name = "user_id")
    private User userId;
    @Column
    private String text;
    @Column
    private int likes;
    @Column(name = "song_time")
    private long songTime;
    @Column(name = "real_time")
    private Date time;
    @Column(name = "parent_id")
    private long parentCommentId;
}
