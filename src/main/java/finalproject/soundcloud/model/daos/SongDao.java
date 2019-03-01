package finalproject.soundcloud.model.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SongDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public void updateSong(){

    }
}
