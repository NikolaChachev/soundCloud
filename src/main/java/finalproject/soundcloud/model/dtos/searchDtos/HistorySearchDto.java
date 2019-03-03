package finalproject.soundcloud.model.dtos.searchDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
@NoArgsConstructor
public class HistorySearchDto extends UserSongDto {
    Timestamp date_and_time;
}
