package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long> {

    User findFirstByUsernameAndPassword(String username , String password);
    User findByUsernameOrEmail(String username,String email);
    User findById(long id);
}
