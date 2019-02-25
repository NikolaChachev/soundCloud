package finalproject.soundcloud.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private boolean isPro;


}
