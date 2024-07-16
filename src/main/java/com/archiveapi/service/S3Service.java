package com.archiveapi.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(S3Service.class);

	/** The bucket name. */
	@Value("${S3_BUCKET_NAME}")
	private String bucketName;

	/** The url expiry minutes. */
	@Value("${S3_URL_EXPIRE_MINUTES}")
	private long urlExpiryMinutes = 5;

	/** The s 3 client. */
	@Autowired
	private AmazonS3 s3Client;

	/**
	 * Download file.
	 *
	 * @param key the key
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
	
	
	public void deleteFile(String key) throws IOException {
		s3Client.deleteObject(bucketName, key);
	}

	/**
	 * Put object.
	 *
	 * @param key the key
	 * @param zipBytes the zip bytes
	 * @param metaData the meta data
	 */
	public void putObject(String key, byte[] zipBytes, ObjectMetadata metaData) {
		s3Client.putObject(bucketName, key, new ByteArrayInputStream(zipBytes), metaData);
	}

	/**
	 * Put object 3.
	 *
	 * @param file the file
	 * @param filePath the file path
	 */
	public void putObject3(File file, String filePath) {
		s3Client.putObject(bucketName, filePath, file);
	}

	/**
	 * Gets the object from S 3.
	 *
	 * @param key the key
	 * @return the object from S 3
	 */
	public S3Object getObjectFromS3(String key) {
		return s3Client.getObject(bucketName, key);
	}

	/**
	 * Gets the pre signed download url.
	 *
	 * @param objectPath the object path
	 * @return the pre signed download url
	 */
	public String getPreSignedDownloadUrl(String objectPath) {



		Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * urlExpiryMinutes; // default 5 mins expiration time
		expiration.setTime(expTimeMillis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
				objectPath).withMethod(HttpMethod.GET).withExpiration(expiration);
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();

	}

	/**
	 * Gets the pre signed delete url.
	 *
	 * @param objectPath the object path
	 * @return the pre signed delete url
	 */
	public String getPreSignedDeleteUrl(String objectPath) {

	
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
