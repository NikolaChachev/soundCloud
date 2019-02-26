package finalproject.soundcloud.model.daos;


import finalproject.soundcloud.controller.UserController;
import finalproject.soundcloud.model.dtos.UserEditDto;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void updateUser(UserEditDto editDto, User user, long userId) throws SoundCloudException {
        String sql = "UPDATE users SET username=?, password=?,profile_picture=?," +
                "email=?,first_name=?,second_name=?,city_name=?,country=? where user_id = ?; ";
        String username = validateString(editDto.getUsername()) || editDto.getUsername()== "" ? user.getUsername():editDto.getUsername();
        String password = UserController.isValidPassword(editDto.getPassword())
                && editDto.getPassword() != null ? editDto.getPassword():user.getPassword();
        String profilePicture = validateString(editDto.getPicturePath()) ? user.getProfilePicture():editDto.getPicturePath();
        String email = validateString(editDto.getEmail()) && UserController.isValidEmailAddress(editDto.getEmail())?
                user.getEmail():editDto.getEmail();
        String firstName = validateString(editDto.getFirstName()) ? user.getFirstName():editDto.getFirstName();
        String secondName = validateString(editDto.getSecondName())? user.getSecondName():editDto.getSecondName();
        String city = validateString(editDto.getCity())? user.getCity():editDto.getCity();
        String country = validateString(editDto.getCountry()) ? user.getCountry():editDto.getCountry();
        jdbcTemplate.update(sql,username,password,profilePicture,email,firstName,secondName,city,country,userId);
    }

    //todo better validation
    private boolean validateString(String str){
        if(str == null || str == "" || str.contains(" ")){
            return true;
        }
        return false;
    }
}
