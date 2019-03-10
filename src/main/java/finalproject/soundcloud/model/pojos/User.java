package finalproject.soundcloud.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


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
    @JsonIgnore
    private String password;
    @Column(name = "profile_picture")
    @JsonIgnore
    private String profilePicture;
    @Column
    private String email;
    @Column(name = "user_type")
    private int userType;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "city_name")
    private String city;
    @Column
    private String country;
    @Column(name = "activation_key")
    @JsonIgnore
    private String activationKey;
    @Column
    @JsonIgnore
    private int is_active;

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
