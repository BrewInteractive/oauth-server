package com.brew.oauth20.server.provider.filestorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileStorageProviderS3Test {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3StorageProvider s3StorageProvider;

    private static Stream<Arguments> store_should_throw_illegal_argument_exception() {
        byte[] sampleByteArray = new byte[]{1, 2, 3, 4, 5};

        return Stream.of(
                Arguments.of(
                        null,
                        "path/to/file.txt"
                ),
                Arguments.of(
                        new byte[0],
                        "path/to/file.txt"
                ),
                Arguments.of(
                        sampleByteArray,
                        ""
                ),
                Arguments.of(
                        sampleByteArray,
                        null
                )
        );
    }

    @Test
    void store_should_upload_file_to_s3_and_return_presigned_url() throws IOException {
        // Arrange
        byte[] fileBytes = {1, 2, 3};
        String filePath = "path/to/file.txt";
        String expectedUrl = "https://example.com/file.txt";

        PutObjectResult putObjectResult = mock(PutObjectResult.class);
        when(amazonS3.putObject(eq(s3StorageProvider.awsS3ServiceBucket), eq(filePath), any(), any())).thenReturn(putObjectResult);
        when(amazonS3.generatePresignedUrl(eq(s3StorageProvider.awsS3ServiceBucket), eq(filePath), any())).thenReturn(new URL(expectedUrl));

        // Act
        String result = s3StorageProvider.store(fileBytes, filePath);

        // Assert
        verify(amazonS3).putObject(eq(s3StorageProvider.awsS3ServiceBucket), eq(filePath), any(), any());
        verify(amazonS3).generatePresignedUrl(eq(s3StorageProvider.awsS3ServiceBucket), eq(filePath), any());
        Assertions.assertEquals(expectedUrl, result);
    }

    @MethodSource
    @ParameterizedTest
    void store_should_throw_illegal_argument_exception(byte[] fileBytes, String filePath) {
        // Act and Assert
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> s3StorageProvider.store(fileBytes, filePath));
    }

    @Test
    void store_should_throw_exception_when_file_upload_to_s3_fails() {
        // Arrange
        byte[] fileBytes = {1, 2, 3};
        String filePath = "path/to/file.txt";
        String errorMessage = "Failed to upload file";
        AmazonS3Exception exception = new AmazonS3Exception(errorMessage);
        IOException ioException = new IOException("Failed to upload file to S3: " + errorMessage, exception);

        when(amazonS3.putObject(eq(s3StorageProvider.awsS3ServiceBucket), eq(filePath), any(), any())).thenThrow(exception);

        // Act and Assert
        IOException thrownException = Assertions.assertThrows(IOException.class,
                () -> s3StorageProvider.store(fileBytes, filePath));
        Assertions.assertEquals(ioException.getCause(), thrownException.getCause());
    }
}