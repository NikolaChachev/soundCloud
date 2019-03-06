package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.PlaylistDao;
import finalproject.soundcloud.model.dtos.PlaylistDto;
import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Playlist;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.PlaylistRepository;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import finalproject.soundcloud.util.exceptions.UnauthorizedUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class PlaylistController extends SessionManagerController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    PlaylistDao playlistDao;
    @Autowired
    PlaylistRepository playlistRepository;

    @PostMapping(value = "users/{id}/createPlaylist")
    public ResponseDto createPlaylist(@RequestBody PlaylistDto playlistDto, HttpSession session, @PathVariable("id") long userId) throws SoundCloudException {
        User user = getLoggedUser(session);
        if (user.getId() != userId) {
            throw new UnauthorizedUserException();
        }
        playlistDao.createPlaylist(user, playlistDto);
        return new ResponseDto("playlist created!");
    }

    @DeleteMapping(value = "users/{id}/playlists/{plId}")
    public ResponseDto deletePlaylist(@PathVariable("plId") long playlistId, @PathVariable("id") long userId,
                                      HttpSession session) throws SoundCloudException {
        User user = getLoggedUser(session);
        Playlist playlist = playlistRepository.getById(playlistId);
        if (playlist == null) {
            throw new DoesNotExistException("playlist");
        }
        if (user.getId() != userId) {
            throw new UnauthorizedUserException();
        }
        return playlistDao.deletePlaylist(user, playlist);
    }

    @PostMapping(value = "users/{id}/playlists/{plId}/addSong/{sId}")
    public ResponseDto addSongToPlaylist(@PathVariable("plId") long playlistId, @PathVariable("sId") long songId, HttpSession session) throws SoundCloudException {
        User user = getLoggedUser(session);
        Playlist playlist = playlistRepository.getById(playlistId);
        if (playlist == null) {
            throw new DoesNotExistException("playlist");
        }
        if (playlist.getUserId() != user.getId()) {
            throw new UnauthorizedUserException();
        }
        return playlistDao.addSong(playlistId, songId);
    }

    @DeleteMapping(value = "users/{id}/playlists/{plId}/songs/{sId}")
    public ResponseDto removeSongFromPlaylist(@PathVariable("id") long userId, @PathVariable("sId") long songId, @PathVariable("plId") long playistId, HttpSession session) throws SoundCloudException {
        User user = getLoggedUser(session);
        if (user.getId() != userId) {
            throw new UnauthorizedUserException();
        }
        if (playlistRepository.getById(playistId) == null) {
            throw new DoesNotExistException("playlist");
        }
        if (!playlistDao.removeSong(playistId, songId)) {
            return new ResponseDto("oops something went wrong! try again later");
        }
        return new ResponseDto("song removed from playlist!");
    }

    /*@DeleteMapping(value = "users/{id}/playlists")
>>>>>>> Stashed changes
    public ResponseDto deleteAllUserPlaylists(@PathVariable("id") long userId,HttpSession session) throws SoundCloudException {
        getLoggedUser(session);
        User user = (User) session.getAttribute(LOGGED);
        if(user.getId() != userId){
            throw new UnauthorizedUserException();
        }
        return new ResponseDto("all playlists removed!");
    }*/
}
