package finalproject.soundcloud.model.pojos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "playlists")
public class Playlist {
    @Column(name = "playlist_id")
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "playlist_name")
    private String name;
    @Column
    private String wallpaper;
    @Column(name = "is_public")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isPublic;
}
