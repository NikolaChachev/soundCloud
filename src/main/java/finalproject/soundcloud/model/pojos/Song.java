package finalproject.soundcloud.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Song {
    private int id;
    private User user;
    private String name;
    private int likes;
    private int dislikes;
    private String filePath;
    private String picture;
    private int length;
    private boolean isPublic;
}
