package com.brew.oauth20.server.provider.filestorage;

import com.brew.oauth20.server.data.enums.FileStorageProvider;

import java.io.IOException;


public abstract class BaseFileStorageProvider {
    protected FileStorageProvider fileStorageProvider;

    public abstract String store(byte[] fileBytes, String filePath) throws IOException;
}
