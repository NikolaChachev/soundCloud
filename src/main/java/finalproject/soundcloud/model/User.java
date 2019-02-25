package finalproject.soundcloud.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class User {
    private long id;
    private String username;
    private String password;
    private String profilePicture;
    private String email;
    private boolean isPro;

}
