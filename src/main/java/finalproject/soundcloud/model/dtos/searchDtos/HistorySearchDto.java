package finalproject.soundcloud.model.dtos.searchDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class HistorySearchDto extends UserSongDto {
    LocalDateTime date_and_time;
}
