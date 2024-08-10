package com.bod.bod.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.FileUploadFailureException;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.exception.InvalidFileTypeException;
import com.bod.bod.verification.entity.Verification;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  private final AmazonS3 amazonS3;
  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String BUCKET;

  @Value("${spring.servlet.multipart.max-file-size}")
  private String maxFileSize;

  @Override
  public String upload(MultipartFile multipartFile) throws IOException {

	if (multipartFile.getSize() > parseSize(maxFileSize)) {
	  throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
	}
	File uploadFile = convertToFile(multipartFile)
		.orElseThrow(() -> new GlobalException(ErrorCode.FILE_CONVERSION_ERROR));
	return uploadToS3(uploadFile);
  }

  @Override
  public void deleteFromS3(String fileName) {
	try {
	  amazonS3.deleteObject(new DeleteObjectRequest(BUCKET, fileName));
	} catch (Exception e) {
	  throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
	}
  }

  @Override
  public String imageUpload(MultipartFile image, String key) {
	if (image.isEmpty()) {
	  throw new GlobalException(ErrorCode.EMPTY_FILE);
	}
	fileSizeExceed(image);
	allowedImageTypes(image);
	try {
	  ObjectMetadata metadata = new ObjectMetadata();
	  metadata.setContentType(image.getContentType());
	  metadata.setContentLength(image.getSize());
	  amazonS3Client.putObject(BUCKET, key + image.getOriginalFilename(), image.getInputStream(), metadata);
	  String imageUrl = amazonS3Client.getResourceUrl(BUCKET, key + image.getOriginalFilename());
	  return imageUrl;
	} catch (IOException e) {
	  throw new FileUploadFailureException("파일 업로드 실패");
	}
  }

  @Override
  public void deleteChallengeImage(Challenge challenge, String key) {
	DeleteObjectRequest request = new DeleteObjectRequest(BUCKET, key + challenge.getImage());
	amazonS3Client.deleteObject(request);
  }

  @Override
  public void deleteVerificationImage(Verification verification, String key) {
	DeleteObjectRequest request = new DeleteObjectRequest(BUCKET, key + verification.getImageName());
	amazonS3Client.deleteObject(request);
  }

  private String uploadToS3(File uploadFile) {
	String fileName = "profile-images" + "/" + UUID.randomUUID() + "-" + uploadFile.getName();
	putS3(uploadFile, fileName);
	deleteLocalFile(uploadFile);
	return fileName;
  }

  private void putS3(File uploadFile, String fileName) {
	amazonS3.putObject(new PutObjectRequest(BUCKET, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
  }

  private void deleteLocalFile(File targetFile) {
	if (targetFile.delete()) {
	  log.info("파일 삭제 성공: {}", targetFile.getName());
	} else {
	  throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
	}
  }

  private Optional<File> convertToFile(MultipartFile file) throws IOException {
	File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
	if (convertFile.createNewFile()) {
	  try (FileOutputStream fos = new FileOutputStream(convertFile)) {
		fos.write(file.getBytes());
	  }
	  return Optional.of(convertFile);
	}
	return Optional.empty();
  }

  private long parseSize(String size) {
	if (size.endsWith("MB")) {
	  return Long.parseLong(size.replace("MB", "")) * 1024 * 1024;
	} else if (size.endsWith("KB")) {
	  return Long.parseLong(size.replace("KB", "")) * 1024;
	} else if (size.endsWith("GB")) {
	  return Long.parseLong(size.replace("GB", "")) * 1024 * 1024 * 1024;
	} else {
	  return Long.parseLong(size);
	}
  }

  private void fileSizeExceed(MultipartFile image) {
	if (image.getSize() > parseSize(maxFileSize)) {
	  throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
	}
  }

  private void allowedImageTypes(MultipartFile image) {
	String fileName = image.getOriginalFilename();
	if (fileName == null || !fileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
	  throw new InvalidFileTypeException("지원되지 않는 파일 형식입니다: " + fileName);
	}
  }
}
