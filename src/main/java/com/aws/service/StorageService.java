package com.aws.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class StorageService {

    Logger log = LoggerFactory.getLogger(StorageService.class);

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private S3Client s3Client;

    public String uploadFileToS3Bucket(MultipartFile file) {
        File convertedFile = this.convertMultiPartToFile(file);
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // Upload directly from bytes
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            log.error("Error while uploading file to s3 bucket");
        }
        convertedFile.delete();
        return "File uploaded successfully: " + fileName;

    }

    public byte[] downloadFileToS3Bucket(String fileName) {
        try {
            // Build the GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // Download and read the file into byte[]
            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                return s3Object.readAllBytes();
            }
        } catch (NoSuchKeyException e) {
            log.error("File not found in S3 bucket with key: {}", fileName, e);
        } catch (SdkClientException e) {
            log.error("AWS SDK error while downloading file: {}", fileName, e);
        } catch (IOException e) {
            log.error("IO error while reading file from S3: {}", fileName, e);
        } catch (Exception e) {
            log.error("Unexpected error while downloading file: {}", fileName, e);
        }
        return null;
    }

    public String deleteFileFromS3Bucket(String fileName){
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file '{}' from bucket '{}'", fileName, bucketName);
            return "File deleted successfully: " + fileName;

        } catch (Exception e) {
            log.error("Failed to delete file '{}' from bucket '{}': {}", fileName, bucketName, e.getMessage(), e);
            return "Failed to delete file: " + fileName;
        }
    }

    private File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error while converting multipart to file");
        }
        return convFile;
    }

}
