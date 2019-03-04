package finalproject.soundcloud.model.dtos.searchDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowerDto {
    long user_id;
    long follower_id;
}
