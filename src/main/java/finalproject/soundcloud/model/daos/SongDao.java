package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Song;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SongDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public ResponseDto likeSong(long songId, User user) throws DoesNotExistException {
        String sql = "SELECT * FROM songs WHERE song_id LIKE ?";
         List<Song> songs =  jdbcTemplate.query(sql,new Object[]{songId},new BeanPropertyRowMapper<>(Song.class));
         if(songs.isEmpty()){
             throw new DoesNotExistException("song");
         }
         sql = "DELETE FROM users_disliked_songs WHERE song_id = ?";
         jdbcTemplate.update(sql,songId);
         sql = " DELETE FROM users_liked_songs WHERE song_id = ?";
         int changed = jdbcTemplate.update(sql,songId);
         if(changed != 0){
             return new ResponseDto("song has been removed from liked!");
         }
         sql = "INSERT INTO users_liked_songs(user_id,song_id) VALUES(?,?)";
         jdbcTemplate.update(sql, user.getId(),songId);
         ResponseDto responseDto = new ResponseDto();
         responseDto.setResponse("the song has been added to your liked songs!");
         return responseDto;
    }
}
