package finalproject.soundcloud.controller;

import finalproject.soundcloud.model.daos.UserDao;
import finalproject.soundcloud.model.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserInformationController extends SessionManagerController {

    @Autowired
    UserDao userDao;

}
