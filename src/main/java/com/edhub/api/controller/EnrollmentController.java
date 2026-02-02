package com.edhub.api.controller;

import com.edhub.api.dto.response.ProgressResponse;
import com.edhub.api.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/subtopics/{subtopicId}/complete")
    public ResponseEntity<Map<String, Object>> completeSubtopic(@PathVariable String subtopicId) {
        return ResponseEntity.ok(enrollmentService.markSubtopicComplete(subtopicId));
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<ProgressResponse> getProgress(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getProgress(enrollmentId));
    }
}