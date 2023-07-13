package com.brew.oauth20.server.provider.filestorageservice;

import com.brew.oauth20.server.data.enums.FileStorageProvider;


public abstract class BaseFileStorageProvider {
    protected FileStorageProvider fileStorageProvider;

    public abstract String store(byte[] fileBytes, String filePath);
}