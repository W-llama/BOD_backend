package com.bod.bod.global.service;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.verification.entity.Verification;
import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

  String upload(MultipartFile multipartFile) throws IOException;

  void deleteFromS3(String fileName);

  @Transactional
  String imageUpload(MultipartFile image, String uniqueFileName);

  @Transactional
  void deleteChallengeImage(Challenge challenge);

  @Transactional
  void deleteVerificationImage(Verification verification);
}
