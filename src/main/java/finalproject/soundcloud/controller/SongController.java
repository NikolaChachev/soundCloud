package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.repostitories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SongController extends SessionManagerController{
    @Autowired
    SongRepository songRepository;
}
