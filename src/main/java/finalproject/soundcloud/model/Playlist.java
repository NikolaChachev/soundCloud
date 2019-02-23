package finalproject.soundcloud.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Playlist {
    private int id;
    private User user;
    private String name;
}
