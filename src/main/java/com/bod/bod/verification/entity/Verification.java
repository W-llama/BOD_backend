package com.bod.bod.verification.entity;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.global.TimeStamp;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "db_verifications")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Verification extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public Verification(String title, String content, String image, Challenge challenge, User user) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.status = Status.PENDING;
        this.challenge = challenge;
        this.user = user;
    }
    public boolean checkUser(User user) {
        if (!this.user.getId().equals(user.getId())) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED_VERIFICATION);
        } return true;
    }
}
