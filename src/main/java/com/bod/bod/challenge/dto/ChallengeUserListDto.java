package com.bod.bod.challenge.dto;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeUserListDto {
    private Long id;
    private String nickname;
    private String name;

    public  ChallengeUserListDto(Challenge challenge, User user){
        this.id = challenge.getId();
        this.nickname = user.getNickname();
        this.name = user.getName();
    }
}
