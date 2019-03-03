package finalproject.soundcloud.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "comment_id")
    private long id;
    @Column(name = "song_id")
    private long songId;
    @Column(name = "user_id")
    private long userId;
    @Column
    private String text;
    @Column
    private int likes;
    @Column(name = "song_time")
    private long songTime;
    @Column(name = "real_time")
    private LocalDateTime time;
    @Column(name = "parent_id")
    private long parentCommentId;
}
