package finalproject.soundcloud.model.repostitories;

import finalproject.soundcloud.model.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsernameOrEmail(String username,String email);
    User findByUsername(String username);
    User findById(long id);
    User findByActivationKey(String activationKey);
    //User findByActivation_key(String activationKey);
}
