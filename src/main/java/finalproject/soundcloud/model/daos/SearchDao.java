package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.searchDtos.PlaylistsSearchDto;
import finalproject.soundcloud.model.dtos.searchDtos.SongSearchDto;
import finalproject.soundcloud.model.dtos.searchDtos.UserSearchDto;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class SearchDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository userRepository;

    public List<UserSearchDto> showAllUsers(String username){
        String sql = "SELECT username FROM users WHERE users.username LIKE ?;";
        List<UserSearchDto> users = jdbcTemplate.query(sql, new Object[]{"%"+username+"%"},
                new BeanPropertyRowMapper<>(UserSearchDto.class));
        return users;
    }

    public List<SongSearchDto> showAllSongs(String songName){
        String sql = "SELECT * FROM songs JOIN users\n" +
                "ON songs.user_id = users.user_id WHERE song_name like ? AND is_public = TRUE ";

        List<SongSearchDto> songs = jdbcTemplate.query(sql, new Object[]{"%"+songName+"%"},
                new BeanPropertyRowMapper<>(SongSearchDto.class));
        return songs;
    }

    public List<PlaylistsSearchDto> showAllPlaylists(String playlist){
        String sql = "SELECT * FROM playlists JOIN users\n" +
                "ON playlists.user_id = users.user_id WHERE playlist_name like ? AND is_public = TRUE";
        List<PlaylistsSearchDto> playlists = jdbcTemplate.query(sql, new Object[]{"%"+playlist+"%"},
                new BeanPropertyRowMapper<>(PlaylistsSearchDto.class));
        return playlists;
    }

    public HashMap<String,List> showAllInformation(String keyWord){
        HashMap<String,List> allInfo = new HashMap<>();
        allInfo.put("users" , showAllUsers(keyWord));
        allInfo.put("songs" , showAllSongs(keyWord));
        allInfo.put("playlists" , showAllPlaylists(keyWord));
        return allInfo;
    }
}
