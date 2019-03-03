package finalproject.soundcloud.model.pojos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private long id;
    @Column
    private String username;
    @Column
    private String password;
    @Column(name = "profile_picture")
    private String profilePicture;
    @Column
    private String email;
    @Column(name = "user_type")
//    @Type(type = "org.hibernate.type.NumericBooleanType")
    private int userType;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "city_name")
    private String city;
    private String country;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType+
                '}';
    }
}
