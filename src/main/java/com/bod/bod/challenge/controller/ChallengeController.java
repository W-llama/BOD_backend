package com.bod.bod.challenge.controller;

import com.bod.bod.challenge.dto.ChallengeCreateRequestDto;
import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.service.ChallengeService;
import com.bod.bod.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping("/admin/challenge")
    public ResponseEntity<CommonResponseDto<ChallengeResponseDto>> createChallenge (@Valid @RequestBody ChallengeCreateRequestDto reqDto) {
        ChallengeResponseDto resDto = challengeService.createChallenge(reqDto);
        return ResponseEntity.ok().body(new CommonResponseDto<>("챌린지 등록에 성공하였습니다!", resDto));
    }

}
