package com.bootcamptoprod.mcp;

import com.bootcamptoprod.dto.FileMetadata;
import com.bootcamptoprod.dto.FileOperationRequest;
import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * File Operation MCP Server - Direct file operations without AI agent capabilities.
 * Provides 4 core file operations: create, read, edit, delete with rich metadata.
 * Uses FileOperationRequest as input parameter for all operations.
 */
@Agent(description = "An agent capable of managing files on the local file system.")
public class FileOperationsMcp {

    private static final Logger logger = LoggerFactory.getLogger(FileOperationsMcp.class);

    private static final Path BASE_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

    /**
     * Create a file. Fails if the file already exists to prevent accidental overwrite.
     */
    @Action
    @AchievesGoal(
            description = "Create a new file with specific content.",
            export = @Export(remote = true, name = "createFile", startingInputTypes = {FileOperationRequest.class})
    )
    public FileMetadata createFile(FileOperationRequest request) {
        logger.info("Request received: Create file '{}'", request.fileName());
        try {
            Path path = resolvePath(request.fileName());

            // Safety check: Don't overwrite existing files in create mode
            if (Files.exists(path)) {
                logger.warn("Creation failed: File '{}' already exists", request.fileName());
                return FileMetadata.error(request.fileName(), "File already exists.");
            }

            String content = request.fileContent();

            // Write content and create new file
            Files.writeString(path, content, StandardOpenOption.CREATE_NEW);
            logger.info("Success: Created file '{}'", request.fileName());

            return FileMetadata.fromPath(path, content, false);
        } catch (Exception e) {
            return handleError("create", request.fileName(), e);
        }
    }

    /**
     * Read file content. Fails if file is missing.
     */
    @Action
    @AchievesGoal(
            description = "Read the contents of a file.",
            export = @Export(remote = true, name = "readFile", startingInputTypes = {FileOperationRequest.class})
    )
    public FileMetadata readFile(FileOperationRequest request) {
        logger.info("Request received: Read file '{}'", request.fileName());
        try {
            Path path = resolvePath(request.fileName());

            if (!Files.exists(path)) {
                logger.warn("Read failed: File '{}' not found", request.fileName());
                return FileMetadata.error(request.fileName(), "File not found.");
            }

            String content = Files.readString(path);
            logger.info("Success: Read file '{}' ({} bytes)", request.fileName(), content.length());

            return FileMetadata.fromPath(path, content, false);
        } catch (Exception e) {
            return handleError("read", request.fileName(), e);
        }
    }

    /**
     * Edit/Overwrite a file. Fails if file is missing.
     */
    @Action
    @AchievesGoal(
            description = "Edit/Overwrite an existing file.",
            export = @Export(remote = true, name = "editFile", startingInputTypes = {FileOperationRequest.class})
    )
    public FileMetadata editFile(FileOperationRequest request) {
        logger.info("Request received: Edit file '{}'", request.fileName());
        try {
            Path path = resolvePath(request.fileName());

            if (!Files.exists(path)) {
                logger.warn("Edit failed: File '{}' not found", request.fileName());
                return FileMetadata.error(request.fileName(), "File not found.");
            }

            String content = request.fileContent();

            // Truncate existing allows us to completely replace old content
            Files.writeString(path, content, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Success: Edited file '{}'", request.fileName());

            return FileMetadata.fromPath(path, content, false);
        } catch (Exception e) {
            return handleError("edit", request.fileName(), e);
        }
    }

    /**
     * Delete a file. Returns metadata of the deleted file.
     */
    @Action
    @AchievesGoal(
            description = "Delete a specific file.",
            export = @Export(remote = true, name = "deleteFile", startingInputTypes = {FileOperationRequest.class})
    )
    public FileMetadata deleteFile(FileOperationRequest request) {
        logger.info("Request received: Delete file '{}'", request.fileName());
        try {
            Path path = resolvePath(request.fileName());

            if (!Files.exists(path)) {
                logger.warn("Delete failed: File '{}' not found", request.fileName());
                return FileMetadata.error(request.fileName(), "File not found.");
            }

            // Capture metadata snapshot before deleting so we can return what we destroyed
            FileMetadata deletedMeta = FileMetadata.fromPath(path, null, true);

            Files.delete(path);
            logger.info("Success: Deleted file '{}'", request.fileName());

            return deletedMeta;
        } catch (Exception e) {
            return handleError("delete", request.fileName(), e);
        }
    }

    // --- Helper Methods ---

    /**
     * Ensures filename is valid and resolves to the BASE_DIR.
     */
    private Path resolvePath(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        // Strip directory parts and just take the filename
        String safeName = Paths.get(fileName).getFileName().toString();
        return BASE_DIR.resolve(safeName);
    }

    /**
     * Centralized error logging and response generation.
     */
    private FileMetadata handleError(String operation, String fileName, Exception e) {
        String msg = "Error during " + operation + ": " + e.getMessage();
        logger.error(msg, e);
        return FileMetadata.error(fileName, msg);
    }
}