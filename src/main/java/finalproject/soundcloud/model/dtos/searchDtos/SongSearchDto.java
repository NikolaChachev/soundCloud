package finalproject.soundcloud.model.dtos.searchDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SongSearchDto {
    String song_name;
    String username;
    int likes;
    int dislikes;
    int length;
}
