package finalproject.soundcloud.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "songs")
public class Song implements SoundCloudSearch{
    @Id
    @Column(name = "song_id")
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "song_name")
    private String songName;
    @Column
    private int likes;
    @Column
    private int dislikes;
    @Column(name = "is_public")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isPublic;
    @Column(name = "file_path")
    private String filePath;
    @Column
    private int length;
    @Column
    private String picture;
}
