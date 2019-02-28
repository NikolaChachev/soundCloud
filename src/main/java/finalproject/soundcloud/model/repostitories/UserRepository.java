package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;


public interface UserRepository extends JpaRepository<User,Long> {

    User findFirstByUsernameAndPassword(String username , String password);
    User findByUsernameOrEmail(String username,String email);
    User findByUsername(String username);
    ArrayList<User> findAllUserByUsername(String username);


}
