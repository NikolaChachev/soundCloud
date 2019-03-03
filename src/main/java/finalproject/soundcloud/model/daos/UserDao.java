package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.UserEditDto;
import finalproject.soundcloud.model.dtos.searchDtos.*;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void updateUser(UserEditDto editDto, User user, long userId) throws SoundCloudException {
        String sql = "UPDATE users SET " +
                "username= COALESCE (? , ?) ," +
                "password= COALESCE (? , ?)," +
                "email = COALESCE (? , ?)," +
                "first_name = COALESCE (? , ? , ' ')," +
                "second_name= COALESCE (? , ? , ' ')," +
                "city_name= COALESCE (? , ? , ' ')," +
                "country= COALESCE (? , ? , ' ')" +
                " where user_id = ?; ";

        jdbcTemplate.update(sql,
                UserValidationDao.validateUsername(editDto.getUsername()), user.getUsername(),
                UserValidationDao.validatePassword(editDto.getPassword()) ? editDto.getPassword() : null, user.getPassword(),
                UserValidationDao.validateEmailAddress(editDto.getEmail()) ? editDto.getEmail() : null, user.getEmail(),
                UserValidationDao.validateOtherData(editDto.getFirstName()), user.getFirstName(),
                UserValidationDao.validateOtherData(editDto.getSecondName()), user.getSecondName(),
                UserValidationDao.validateOtherData(editDto.getCity()), user.getCity(),
                UserValidationDao.validateOtherData(editDto.getCountry()), user.getCountry(),
                userId);

    }
    public List<SongSearchDto> getMyUploadedSongs(long userId){
        String sql = "SELECT * FROM songs JOIN users\n" +
                "ON songs.user_id = users.user_id WHERE users.user_id = ?";
        List<SongSearchDto> songs = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(SongSearchDto.class));
        return songs;
    }
    public List<PlaylistsSearchDto> getMyUploadedPlaylists(long userId){
        String sql = "SELECT * FROM playlists JOIN users\n" +
                "ON playlists.user_id = users.user_id WHERE users.user_id = ?";
        List<PlaylistsSearchDto> playlists = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(PlaylistsSearchDto.class));
        return playlists;
    }
    public List<UserSongDto> getMyReposts(long userId) {
        String sql = "SELECT username,song_name FROM users_reposts JOIN users \n" +
                "on users.user_id = users_reposts.user_id\n" +
                "JOIN songs\n" +
                "on songs.song_id = users_reposts.song_id WHERE user_id = ?";

        List<UserSongDto> reposts = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(UserSongDto.class));
        return reposts;
    }
    public List<UserSongDto> getMyLikedSongs(long userId){
        String sql = "SELECT username , song_name FROM users_liked_songs\n" +
                "JOIN users ON users.user_id = users_liked_songs.user_id\n" +
                "JOIN songs ON songs.song_id = users_liked_songs.song_id WHERE users.user_id = ?";
        List<UserSongDto> likedSongs = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(UserSongDto.class));
        return likedSongs;
    }
    public List<PlaylistsSearchDto> getMyLikedPlaylists(long userId){
        String sql = "SELECT username , playlist_name FROM users_liked_playlists\n" +
                "JOIN users ON users.user_id = users_liked_playlists.user_id\n" +
                "JOIN playlists ON playlists.playlist_id = users_liked_playlists.playlist_id WHERE users.user_id = ?";
        List<PlaylistsSearchDto> likedPlaylists = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(PlaylistsSearchDto.class));
        return likedPlaylists;
    }
    public List<UserSearchDto> getAllFollowers(long userId){
        String sql = "SELECT username FROM users JOIN followers " +
                "ON followers.follower_id = users.user_id WHERE followers.user_id = ?";

        List<UserSearchDto> followers = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(UserSearchDto.class));
        return followers;
    }
    public List<UserSearchDto> getAllFollowing(long userId){
        String sql = "SELECT username FROM users JOIN followers ON followers.user_id = users.user_id\n" +
                "WHERE followers.follower_id = ?;";

        List<UserSearchDto> following = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(UserSearchDto.class));
        return following;
    }

    //history
    public List<HistorySearchDto> getHistory(long userId){
        String sql = "SELECT username , song_name ,date_and_time FROM users_history\n" +
                "JOIN users ON users.user_id = users_history.user_id\n" +
                "JOIN songs ON songs.song_id = users_history.song_id " +
                "WHERE users.user_id = ?";

        List<HistorySearchDto> history = jdbcTemplate.query(sql, new Object[]{userId},
                new BeanPropertyRowMapper<>(HistorySearchDto.class));
        for(HistorySearchDto dto : history){
            System.out.println(dto);
        }
        return history;
    }
}
