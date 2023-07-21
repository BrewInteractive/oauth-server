package com.brew.oauth20.server.data.enums;

@SuppressWarnings("java:S115")
public enum FileStorageProvider {
    s3("s3");

    private final String value;

    FileStorageProvider(String value) {
        this.value = value;
    }

    public static FileStorageProvider fromValue(String value) {
        for (FileStorageProvider fileStorageProvider : FileStorageProvider.values()) {
            if (fileStorageProvider.getFileStorageProvider().equalsIgnoreCase(value)) {
                return fileStorageProvider;
            }
        }
        return null;
    }

    public String getFileStorageProvider() {
        return value;
    }
}
