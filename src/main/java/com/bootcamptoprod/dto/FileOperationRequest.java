package com.bootcamptoprod.dto;

import org.springaicommunity.mcp.annotation.McpToolParam;

/**
 * Request record for file operations containing file name and content.
 */
public record FileOperationRequest(
        @McpToolParam(description = "File Name") String fileName,
        @McpToolParam(description = "File Content", required = false) String fileContent
) {
}
