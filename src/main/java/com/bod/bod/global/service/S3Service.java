package com.bod.bod.global.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	String upload(MultipartFile multipartFile) throws IOException;

	void deleteFromS3(String fileName);
}
