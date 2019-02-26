package finalproject.soundcloud.model.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLogInDto {
    private String username;
    private String password;
}
