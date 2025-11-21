package com.synkronos.ai.controller;

import com.synkronos.ai.service.FileUploadService;
import com.synkronos.ai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for file upload endpoints
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "File upload APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final UserService userService;

    @PostMapping("/resume")
    @Operation(summary = "Upload resume", description = "Upload PDF resume file")
    public ResponseEntity<Map<String, String>> uploadResume(@RequestParam("file") MultipartFile file,
                                                            Authentication authentication) {
        String email = authentication.getName();
        String userId = userService.getUserByEmail(email).getId();
        String resumeUrl = fileUploadService.uploadResume(file);
        userService.updateResumeUrl(userId, resumeUrl);

        Map<String, String> response = new HashMap<>();
        response.put("resumeUrl", resumeUrl);
        response.put("message", "Resume uploaded successfully");
        return ResponseEntity.ok(response);
    }
}

