package com.brew.oauth20.server.provider.filestorage;

public class MockFileStorageProviderNotExistingInIoc extends BaseFileStorageProvider {

    @Override
    public String store(byte[] fileBytes, String filePath) {
        return null;
    }
}
