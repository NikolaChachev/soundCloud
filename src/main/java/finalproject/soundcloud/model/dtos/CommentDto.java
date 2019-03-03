package finalproject.soundcloud.model.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private String text;
    private long userId;
    private long songId;
    private long songTime;
    private long parentId;
}
