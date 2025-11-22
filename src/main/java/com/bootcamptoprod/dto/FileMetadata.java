package com.bootcamptoprod.dto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

/**
 * Record to hold the result of file operations.
 * Includes helper methods to automatically extract file attributes.
 */
public record FileMetadata(
        String fileName,
        String exactPath,
        long fileSize,
        Instant createdAt,
        Instant updatedAt,
        String content,
        boolean isDeleted,
        String error
) {
    /**
     * Factory method: Creates metadata by reading actual file attributes from disk.
     */
    public static FileMetadata fromPath(Path path, String content, boolean isDeleted) throws IOException {

        // Try to read file attributes (creation time, etc.)
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

        return new FileMetadata(
                path.getFileName().toString(),
                path.toAbsolutePath().toString(),
                content != null ? content.getBytes().length : 0,
                attrs.creationTime().toInstant(),
                attrs.lastModifiedTime().toInstant(),
                content,
                isDeleted,
                null // No error
        );
    }

    /**
     * Factory method: Creates an error response.
     */
    public static FileMetadata error(String fileName, String errorMessage) {
        return new FileMetadata(
                fileName,
                "Unknown",
                0,
                Instant.now(),
                Instant.now(),
                null,
                false,
                errorMessage
        );
    }
}
