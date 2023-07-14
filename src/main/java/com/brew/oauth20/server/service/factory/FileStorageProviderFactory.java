package com.brew.oauth20.server.service.factory;

import com.brew.oauth20.server.data.enums.FileStorageProvider;
import com.brew.oauth20.server.provider.filestorage.BaseFileStorageProvider;
import com.brew.oauth20.server.provider.filestorage.S3StorageProvider;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class FileStorageProviderFactory extends ServiceFactory<FileStorageProvider, BaseFileStorageProvider> {
    public FileStorageProviderFactory() {
        Map<FileStorageProvider, Type> map = Map.of(
                FileStorageProvider.s3, S3StorageProvider.class
        );
        setRegisteredServiceTypes(map);
    }
}