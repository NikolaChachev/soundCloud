package finalproject.soundcloud.model.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserActivationDto extends UserLogInDto {
    String activation_key;
}
