package com.bod.bod.user.entity;

import com.bod.bod.global.TimeStamp;
import com.bod.bod.userChallenge.UserChallenge;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "db_users")
public class User extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "introduce")
    private String introduce;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "point")
    private Long point = 0L;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "userRole", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserChallenge> userChallenges;

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void changeImage(String image) {
        this.image = image;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
