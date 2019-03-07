package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    User findById(long id);
    User findByActivationKey(String activationKey);
    User findByUsernameOrEmail(String username,String email);
}
