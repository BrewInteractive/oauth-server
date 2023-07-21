package com.brew.oauth20.server.provider.filestorage;

import com.brew.oauth20.server.data.enums.FileStorageProvider;
import com.brew.oauth20.server.exception.MissingServiceException;
import com.brew.oauth20.server.exception.UnsupportedServiceTypeException;
import com.brew.oauth20.server.service.factory.FileStorageProviderFactory;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileStorageProviderFactoryTest {

    @Autowired
    FileStorageProviderFactory fileStorageProviderFactory;

    @Test
    void should_return_s3_file_storage_provider_object() {
        var service = fileStorageProviderFactory.getService(FileStorageProvider.s3);
        assertThat(service).isInstanceOf(S3StorageProvider.class);
    }

    @Test
    void should_throws_unsupported_service_type_exception() {
        fileStorageProviderFactory.setRegisteredServiceTypes(
                Map.of(
                )
        );
        Exception exception = assertThrows(UnsupportedServiceTypeException.class, () -> fileStorageProviderFactory.getService(FileStorageProvider.s3));
        assertThat(exception.getMessage()).isEqualTo(FileStorageProvider.s3.toString());
    }


    @Test
    void should_throws_missing_service_exception() {
        fileStorageProviderFactory.setRegisteredServiceTypes(
                Map.of(
                        FileStorageProvider.s3, MockFileStorageProviderNotExistingInIoc.class
                )
        );
        Exception exception = assertThrows(MissingServiceException.class, () -> fileStorageProviderFactory.getService(FileStorageProvider.s3));
        assertInstanceOf(BeansException.class, exception.getCause());
    }
}

