package com.bod.bod.verification.entity;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.global.TimeStamp;
import com.bod.bod.user.entity.User;
import com.bod.bod.verification.dto.VerificationRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Column(nullable = false)
    private Status status;

    public Verification(VerificationRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
//        this.image = requestDto.getImage();
    }
}
