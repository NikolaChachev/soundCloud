package finalproject.soundcloud.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class Comment {
    private int id;
    private Song song;
    private User user;
    private String text;
    private int likes;
    private Date time;
    private Comment parentComment;
}
