//package com.bod.bod.verification.service;
//
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.bod.bod.challenge.entity.Challenge;
//import com.bod.bod.challenge.service.ChallengeService;
//import com.bod.bod.global.exception.FileUploadFailureException;
//import com.bod.bod.verification.dto.VerificationRequestDto;
//import com.bod.bod.verification.dto.VerificationResponseDto;
//import com.bod.bod.verification.entity.Verification;
//import com.bod.bod.verification.repository.VerificationRepository;
//import java.io.IOException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//@RequiredArgsConstructor
//public class VerificationService {
//
//  private final VerificationRepository verificationRepository;
//  private final ChallengeService challengeService;
//
//  @Value("${cloud.aws.s3.bucket}")
//  private String BUCKET;
//  private final AmazonS3Client amazonS3Client;
//
//  @Transactional
//  public VerificationResponseDto requestVerification(Long challengeId, MultipartFile image, String imageName, VerificationRequestDto requestDto) {
//    try {
//      ObjectMetadata metadata= new ObjectMetadata();
//      metadata.setContentType(image.getContentType());
//      metadata.setContentLength(image.getSize());
//      amazonS3Client.putObject(BUCKET, imageName, image.getInputStream(), metadata);
//
//      Challenge challenge = challengeService.findChallengeById(challengeId);
//
//      Verification verification = new Verification(requestDto.getTitle(), requestDto.getContent(), imageName, challenge);
//      verificationRepository.save(verification);
//
//      String imageUrl= "https://" + BUCKET + imageName;
//      VerificationResponseDto responseDto = new VerificationResponseDto(verification.getTitle(), verification.getContent(), imageUrl);
//      return responseDto;
//    } catch(IOException e) {
//      throw new FileUploadFailureException("파일 업로드 실패");
//    }
//  }
//
////  public void cancelVerification(String imageName){
////    DeleteObjectRequest request = new DeleteObjectRequest(BUCKET, imageName);
////    amazonS3Client.deleteObject(request);
////  }
//
//
//}
