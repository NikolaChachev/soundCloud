package finalproject.soundcloud.model.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEditDto extends UserLogInDto {
    private String email;
    private String firstName;
    private String secondName;
    private String city;
    private String country;
}
