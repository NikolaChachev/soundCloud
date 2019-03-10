package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
    Playlist getById(long id);
    ArrayList<Playlist> getAllByUserId(long id);
    void removeById(long id);
}
