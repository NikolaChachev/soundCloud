package finalproject.soundcloud.model.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDto {
    private String username;
    private String firstPassword;
    private String secondPassword;
    private String email;
    private boolean isPro;
    private String picturePath;
}