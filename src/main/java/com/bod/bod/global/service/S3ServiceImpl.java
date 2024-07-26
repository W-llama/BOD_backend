package com.bod.bod.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
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

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

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

	private String uploadToS3(File uploadFile) {
		String fileName = "profile-images" + "/" + UUID.randomUUID() + "-" + uploadFile.getName();
		putS3(uploadFile, fileName);
		deleteLocalFile(uploadFile);
		return fileName;
	}

	private void putS3(File uploadFile, String fileName) {
		amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	private void deleteLocalFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일 삭제 성공: {}", targetFile.getName());
		} else {
			throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
		}
	}

	@Override
	public void deleteFromS3(String fileName) {
		try {
			amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
		} catch (Exception e) {
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
}
