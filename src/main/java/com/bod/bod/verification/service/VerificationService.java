package com.bod.bod.verification.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.service.ChallengeService;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.FileUploadFailureException;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.verification.dto.VerificationRequestDto;
import com.bod.bod.verification.dto.VerificationResponseDto;
import com.bod.bod.verification.dto.VerificationTop3UserResponseDto;
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import com.bod.bod.verification.entity.Verification;
import com.bod.bod.verification.repository.VerificationRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VerificationService {

  private final VerificationRepository verificationRepository;
  private final ChallengeService challengeService;
  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String BUCKET;

  @Transactional
  public VerificationResponseDto requestVerification(Long challengeId, MultipartFile image, VerificationRequestDto requestDto, User user) {
    try {
      ObjectMetadata metadata= new ObjectMetadata();
      metadata.setContentType(image.getContentType());
      metadata.setContentLength(image.getSize());
      amazonS3Client.putObject(BUCKET, "verification/" + image.getOriginalFilename(), image.getInputStream(), metadata);

      Challenge challenge = challengeService.findById(challengeId);

      LocalDateTime currentDate = LocalDateTime.now();
      LocalDateTime startOfDay = currentDate.toLocalDate().atStartOfDay();
      LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

      List<Verification> existingVerificationList = verificationRepository.findByCreatedAtBetweenAndUser(startOfDay, endOfDay, user);

      if(!existingVerificationList.isEmpty()) {
        throw new GlobalException(ErrorCode.ALREADY_EXISTS_VERIFICATION);
      }

      String imageUrl= amazonS3Client.getResourceUrl(BUCKET, "verification/" + image.getOriginalFilename());
      Verification verification = new Verification(requestDto.getTitle(), requestDto.getContent(), image.getOriginalFilename(), imageUrl, challenge, user);
      verificationRepository.save(verification);

      VerificationResponseDto responseDto = new VerificationResponseDto(verification.getId(), verification.getTitle(), verification.getContent(), imageUrl, verification.getStatus());
      return responseDto;

    } catch(IOException e) {
      throw new FileUploadFailureException("파일 업로드 실패");
    }
  }

  @Transactional
  public void cancelVerification(Long verificationId, User user) {
    Verification verification = findVerificationById(verificationId);
    verification.checkUser(user);
    DeleteObjectRequest request = new DeleteObjectRequest(BUCKET, "verification/" + verification.getImageName());
    amazonS3Client.deleteObject(request);
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

  public Verification findVerificationById(Long verificationId) {
    return verificationRepository.findVerificationById(verificationId);
  }

}
