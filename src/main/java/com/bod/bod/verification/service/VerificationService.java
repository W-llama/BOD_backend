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
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import com.bod.bod.verification.entity.Verification;
import com.bod.bod.verification.repository.VerificationRepository;
import java.io.IOException;
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

  @Value("${cloud.aws.s3.bucket}")
  private String BUCKET;
  private final AmazonS3Client amazonS3Client;

  @Transactional
  public VerificationResponseDto requestVerification(Long challengeId, MultipartFile image, VerificationRequestDto requestDto, User user) {
    try {
      ObjectMetadata metadata= new ObjectMetadata();
      metadata.setContentType(image.getContentType());
      metadata.setContentLength(image.getSize());
      amazonS3Client.putObject(BUCKET, image.getOriginalFilename(), image.getInputStream(), metadata);

      Challenge challenge = challengeService.findChallengeById(challengeId);

      Verification verification = new Verification(requestDto.getTitle(), requestDto.getContent(), image.getOriginalFilename(), challenge, user);
      verificationRepository.save(verification);
      String imageUrl= amazonS3Client.getResourceUrl(BUCKET, image.getOriginalFilename());

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
    DeleteObjectRequest request = new DeleteObjectRequest(BUCKET, verification.getImage());
    amazonS3Client.deleteObject(request);
    verificationRepository.delete(verification);
  }

  @Transactional(readOnly = true)
  public List<VerificationWithUserResponseDto> getVerificationsByChallengeId(int page, Long challengeId) {
    Challenge challenge = challengeService.findChallengeById(challengeId);
    List<VerificationWithUserResponseDto> responseDto = verificationRepository.findVerificationWithUserByChallengeId(page, challengeId);
    if(responseDto.isEmpty()) {
      throw new GlobalException(ErrorCode.EMPTY_VERIFICATION);
    }
    return responseDto;
  }

  public Verification findVerificationById(Long verificationId) {
    return verificationRepository.findVerificationById(verificationId);
  }

}
