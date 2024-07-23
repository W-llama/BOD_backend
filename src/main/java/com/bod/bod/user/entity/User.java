package com.bod.bod.user.entity;

import com.bod.bod.global.TimeStamp;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "introduce",nullable = true)
    private String introduce;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

//    @Column(name = "role", nullable = false)
//    private Role role;

    @Column(name = "point",nullable = true)
    private Long point;

    @Column(name = "image",nullable = false)
    private String image;

    @Column(name = "refreshToken",nullable = true)
    private String refreshToken;

}
