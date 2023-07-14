package com.brew.oauth20.server.provider.filestorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.brew.oauth20.server.data.enums.FileStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@Component
public class S3StorageProvider extends BaseFileStorageProvider {

    public S3StorageProvider(){
        fileStorageProvider = FileStorageProvider.s3;
    }

    @Value("${s3.service.aws.bucket}")
    String awsS3ServiceBucket;
    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public String store(byte[] fileBytes, String filePath) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("File bytes are missing or empty.");
        }

        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path is missing or empty.");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileBytes.length);

            amazonS3.putObject(awsS3ServiceBucket, filePath, inputStream, metadata);
        } catch (AmazonS3Exception e) {
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }

        Date expiration = new Date(System.currentTimeMillis() + 60000);
        return amazonS3.generatePresignedUrl(awsS3ServiceBucket, filePath, expiration).toString();
    }
}
