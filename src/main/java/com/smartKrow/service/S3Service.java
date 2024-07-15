package com.smartKrow.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class S3Service {

	private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

	@Value("${amazon.aws.s3.bucket-name}")
	private String bucketName;

	@Value("${amazon.aws.s3.expire-url-minutes}")
	private long urlExpiryMinutes = 5;

	@Autowired
	private AmazonS3 s3Client;

	public String downloadFile(String key) throws IOException {
		S3Object s3Object = s3Client.getObject(bucketName, key);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		byte[] content = IOUtils.toByteArray(inputStream);
		String filePath = "temporaryCsv.csv";
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(content);
			fos.close();
			logger.info("File has been written successfully!");
		} catch (IOException e) {
			logger.info("Error writing file: " + e.getMessage());
			e.printStackTrace();
		}
		return filePath;
	}

	public void putObject(String key, byte[] zipBytes, ObjectMetadata metaData) {
		s3Client.putObject(bucketName, key, new ByteArrayInputStream(zipBytes), metaData);
	}

	public void putObject3(File file, String filePath) {
		s3Client.putObject(bucketName, filePath, file);
	}

	public S3Object getObjectFromS3(String key) {
		return s3Client.getObject(bucketName, key);
	}

	public String getPreSignedDownloadUrl(String objectPath) {

		logger.debug("*** Invoked getPreSignedDownloadUrl with expire-url-minutes {} ***", urlExpiryMinutes);

		Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * urlExpiryMinutes; // default 5 mins expiration time
		expiration.setTime(expTimeMillis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
				objectPath).withMethod(HttpMethod.GET).withExpiration(expiration);
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();

	}

	public String getPreSignedDeleteUrl(String objectPath) {

		logger.debug("*** Invoked getPreSignedDeleteUrl with expire-url-minutes {} ***", urlExpiryMinutes);

		// TODO validate input params

		Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * urlExpiryMinutes; // default 5 mins expiration time
		expiration.setTime(expTimeMillis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
				objectPath).withMethod(HttpMethod.DELETE).withExpiration(expiration);
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();

	}

}
