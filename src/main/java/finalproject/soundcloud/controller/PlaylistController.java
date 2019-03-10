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

    @PostMapping(value = "playlists")
    public ResponseDto createPlaylist(@RequestBody PlaylistDto playlistDto, HttpSession session) throws SoundCloudException {
        User user = getLoggedUser(session);
        playlistDao.createPlaylist(user, playlistDto);
        return new ResponseDto("playlist created!");
    }

    @DeleteMapping(value = "playlists/{plId}")
    public ResponseDto deletePlaylist(@PathVariable("plId") long playlistId,
                                      HttpSession session) throws SoundCloudException {
        User user = getLoggedUser(session);
        Playlist playlist = playlistRepository.getById(playlistId);
        if (playlist == null) {
            throw new DoesNotExistException("playlist");
        }
        if (user.getId() != playlist.getUserId()) {
            throw new UnauthorizedUserException();
        }
        return playlistDao.deletePlaylist(user, playlist);
    }

    @PostMapping(value = "playlists/{plId}/addSong/{sId}")
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

    @DeleteMapping(value = "playlists/{plId}/songs/{sId}")
    public ResponseDto removeSongFromPlaylist(@PathVariable("sId") long songId, @PathVariable("plId") long playistId, HttpSession session) throws SoundCloudException {
        User user = getLoggedUser(session);
        if (playlistRepository.getById(playistId) == null) {
            throw new DoesNotExistException("playlist");
        }
        if (user.getId() != playlistRepository.getById(playistId).getUserId()) {
            throw new UnauthorizedUserException();
        }
        if (!playlistDao.removeSong(playistId, songId)) {
            return new ResponseDto("no such song in the playlist!");
        }
        return new ResponseDto("song removed from playlist!");
    }

}
