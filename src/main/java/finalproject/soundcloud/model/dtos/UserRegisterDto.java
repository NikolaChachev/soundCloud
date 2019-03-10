package finalproject.soundcloud.model.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    private long id;
    private String username;
    private String firstPassword;
    private String secondPassword;
    private int userType;
    private String email;
}
