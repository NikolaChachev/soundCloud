package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.SearchDao;
import finalproject.soundcloud.model.dtos.searchDtos.PlaylistsSearchDto;
import finalproject.soundcloud.model.dtos.searchDtos.SongSearchDto;
import finalproject.soundcloud.model.dtos.searchDtos.UserSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    SearchDao searchDao;
    @GetMapping(value = "/search/users/{username}")
    public List<UserSearchDto> searchUsers (@PathVariable("username") String username)
    {
        return searchDao.showAllUsers(username);
    }
    @GetMapping(value = "/search/songs/{song_name}")
    public List<SongSearchDto> searchSongs (@PathVariable("song_name") String song_name)
    {
        return searchDao.showAllSongs(song_name);
    }
    @GetMapping(value = "/search/playlists/{playlists}")
    public List<PlaylistsSearchDto> searchPlaylists (@PathVariable("playlists") String playlists)
    {
        return searchDao.showAllPlaylists(playlists);
    }
    @GetMapping(value = "/search/all/{keyword}")
    public Map<String,List> searchAll (@PathVariable("keyword") String keyword)
    {
        return searchDao.showAllInformation(keyword);
    }
}
