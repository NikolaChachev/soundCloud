package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.PlaylistDto;
import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Playlist;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
public class PlaylistDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    PlaylistRepository playlistRepository;
    public void createPlaylist(User user, PlaylistDto playlistDto){
        Playlist playlist = new Playlist();
        playlist.setName(playlistDto.getName());
        playlist.setUserId(user.getId());
        playlist.setPublic(playlistDto.isPublic());
        playlistRepository.save(playlist);
    }
    @Transactional
    public ResponseDto deletePlaylist(User user, Playlist playlist)  {
        String sql = "DELETE FROM playlists_songs WHERE playlist_id = ?";
        jdbcTemplate.update(sql,playlist.getId());
        playlistRepository.removeById(playlist.getId());
        return new ResponseDto("playlist deleted!");
    }

    public ResponseDto addSong(long playlistId, long songId) {
        String sql = "SELECT * FROM playlists_songs WHERE playlist_id = ? AND song_id = ?";
        RowCountCallbackHandler callbackHandler = new RowCountCallbackHandler();
        jdbcTemplate.query(sql,new Object[]{playlistId,songId},callbackHandler);
        int rowCount = callbackHandler.getRowCount();
        if(rowCount != 0){
            return new ResponseDto("song already in the album!");
        }
        sql = "INSERT INTO playlists_songs(playlist_id,song_id) VALUES(?,?)";
        jdbcTemplate.update(sql,playlistId,songId);
        return new ResponseDto("song successfully added");
    }

    public boolean removeSong(long playistId, long songId) {
        String sql = "DELETE FROM playlists_songs WHERE playlist_id = ? AND song_id = ?";
        int done = jdbcTemplate.update(sql,playistId,songId);
        return done != 0;
    }
    public boolean deleteAllUserPlaylists(User user)  {
        ArrayList<Playlist> playlists = playlistRepository.getAllByUserId(user.getId());
        for (Playlist p : playlists){
            deletePlaylist(user,p);
        }
        return true;
    }
    public boolean removeSongFromAllPlaylists(long songId){
        String sql = "DELETE FROM playlists_songs WHERE song_id = ?";
        int done = jdbcTemplate.update(sql,songId);
        return done != 0;
    }
}
