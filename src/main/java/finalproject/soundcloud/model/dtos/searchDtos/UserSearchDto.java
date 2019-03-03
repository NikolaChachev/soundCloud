package finalproject.soundcloud.model.dtos.searchDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchDto {
    String username;
    int followers_count;
    int songs_count;
}
