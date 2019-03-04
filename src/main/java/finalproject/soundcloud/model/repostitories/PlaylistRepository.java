package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
    Playlist getById(long id);
}
