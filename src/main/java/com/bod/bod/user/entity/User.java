package com.bod.bod.user.entity;

import com.bod.bod.global.TimeStamp;
import com.bod.bod.userchallenge.entity.UserChallenge;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import lombok.*;


@Table(name = "db_users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
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

    @Builder.Default
    @Column(name = "point")
    private Long point = 0L;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();

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

    public void increasePoint() {
        this.point += 500L;
    }

    public void decreasePoint() {
        this.point -= 500L;
    }

    public void changePoint(Long point) {
        this.point = point;
    }

}
