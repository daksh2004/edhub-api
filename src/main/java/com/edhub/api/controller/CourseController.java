package com.edhub.api.controller;

import com.edhub.api.dto.response.CourseListResponse;
import com.edhub.api.entity.Course;
import com.edhub.api.service.CourseService;
import com.edhub.api.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<Map<String, List<CourseListResponse>>> getAllCourses() {
        return ResponseEntity.ok(Map.of("courses", courseService.getAllCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Map<String, Object>> enroll(@PathVariable String courseId) {
        return ResponseEntity.ok(enrollmentService.enroll(courseId));
    }
}