package com.bod.bod.verification.service;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.service.ChallengeService;
import com.bod.bod.global.dto.PaginationResponse;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.service.S3Service;
import com.bod.bod.user.entity.User;
import com.bod.bod.verification.dto.VerificationRequestDto;
import com.bod.bod.verification.dto.VerificationResponseDto;
import com.bod.bod.verification.dto.VerificationTop3UserResponseDto;
import com.bod.bod.verification.dto.VerificationWithChallengeResponseDto;
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import com.bod.bod.verification.entity.Status;
import com.bod.bod.verification.entity.Verification;
import com.bod.bod.verification.repository.VerificationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VerificationService {

  private final VerificationRepository verificationRepository;
  private final ChallengeService challengeService;
  private final S3Service s3Service;

  @Transactional
  public VerificationResponseDto requestVerification(Long challengeId, MultipartFile image, VerificationRequestDto requestDto, User user) {
      Challenge challenge = challengeService.findById(challengeId);

      duplicateVerification(challengeId, user);

      String key = "verification/";
      String uniqueFileName = key + UUID.randomUUID() + "_" + image.getOriginalFilename();
      String imageUrl = s3Service.imageUpload(image, uniqueFileName);
      Verification verification = new Verification(requestDto.getTitle(), requestDto.getContent(), uniqueFileName, imageUrl, challenge, user);
      verificationRepository.save(verification);
      return new VerificationResponseDto(verification.getId(), verification.getTitle(), verification.getContent(), imageUrl, verification.getStatus());
  }

  @Transactional
  public void cancelVerification(Long verificationId, User user) {
    Verification verification = findVerificationById(verificationId);
    if(verification.getStatus().equals(Status.APPROVE)) {
      throw new GlobalException(ErrorCode.DO_NOT_CANCEL_VERIFICATION);
    }
    verification.checkUser(user);
    s3Service.deleteVerificationImage(verification);
    verificationRepository.delete(verification);
  }

  @Transactional(readOnly = true)
  public List<VerificationWithUserResponseDto> getVerificationsByChallengeId(int page, Long challengeId) {
    Challenge challenge = challengeService.findById(challengeId);
    List<VerificationWithUserResponseDto> responseDto = verificationRepository.findVerificationWithUserByChallengeId(page, challengeId);
    if(responseDto.isEmpty()) {
      throw new GlobalException(ErrorCode.EMPTY_VERIFICATION);
    }
    return responseDto;
  }

  @Transactional(readOnly = true)
  public List<VerificationTop3UserResponseDto> getTop3VerificationUsers(Long challengeId) {
    Challenge challenge = challengeService.findById(challengeId);
    List<VerificationTop3UserResponseDto> responseDto = verificationRepository.getTop3VerificationUsers(challengeId);
    if(responseDto.isEmpty()) {
      throw new GlobalException(ErrorCode.EMPTY_VERIFICATION);
    }
    return responseDto;
  }

  @Transactional(readOnly = true)
  public PaginationResponse<VerificationWithChallengeResponseDto> getVerficationsByUser(int page, int size, User user) {
    Pageable pageable = PageRequest.of(page, size);
    Page<VerificationWithChallengeResponseDto> verificationListByUser = verificationRepository.getVerificationsByUser(pageable, user);
    if(verificationListByUser.isEmpty()) {
      throw new GlobalException(ErrorCode.NOT_FOUND_USER_VERIFICATION);
    }

    return new PaginationResponse<>(
        verificationListByUser.getContent(),
        verificationListByUser.getTotalPages(),
        verificationListByUser.getTotalElements(),
        verificationListByUser.getNumber(),
        verificationListByUser.getSize()
    );
  }

  public Verification findVerificationById(Long verificationId) {
    return verificationRepository.findVerificationById(verificationId);
  }

  private void duplicateVerification(Long challengeId, User user) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    LocalDate currentDate = currentDateTime.toLocalDate();
    LocalDateTime startOfDay = currentDate.atStartOfDay();
    LocalDateTime endOfDay = currentDate.atTime(LocalTime.MAX);

    boolean checkVerification = verificationRepository.existsByChallengeIdAndUserAndCreatedAtBetween(
        challengeId, user, startOfDay, endOfDay
    );

    if(checkVerification) {
      throw new GlobalException(ErrorCode.ALREADY_EXISTS_VERIFICATION);
    }
  }
}
