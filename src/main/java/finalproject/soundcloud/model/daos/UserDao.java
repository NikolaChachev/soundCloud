package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.UserEditDto;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.util.exceptions.SoundCloudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void updateUser(UserEditDto editDto, User user, long userId) throws SoundCloudException {
        String sql = "UPDATE users SET " +
                "username= COALESCE (? , ?) ," +
                "password= COALESCE (? , ?)," +
                "email = COALESCE (? , ?)," +
                "first_name = COALESCE (? , ? , ' ')," +
                "second_name= COALESCE (? , ? , ' ')," +
                "city_name= COALESCE (? , ? , ' ')," +
                "country= COALESCE (? , ? , ' ')" +
                " where user_id = ?; ";

        jdbcTemplate.update(sql,
                UserValidationDao.validateUsername(editDto.getUsername()), user.getUsername(),
                UserValidationDao.validatePassword(editDto.getPassword()) ? editDto.getPassword() : null, user.getPassword(),
                UserValidationDao.validateEmailAddress(editDto.getEmail()) ? editDto.getEmail() : null, user.getEmail(),
                UserValidationDao.validateOtherData(editDto.getFirstName()), user.getFirstName(),
                UserValidationDao.validateOtherData(editDto.getSecondName()), user.getSecondName(),
                UserValidationDao.validateOtherData(editDto.getCity()), user.getCity(),
                UserValidationDao.validateOtherData(editDto.getCountry()), user.getCountry(),
                userId);

    }


}
