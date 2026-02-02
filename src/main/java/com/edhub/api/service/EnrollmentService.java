package com.edhub.api.service;

import com.edhub.api.dto.response.ProgressResponse;
import com.edhub.api.entity.*;
import com.edhub.api.exception.*;
import com.edhub.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SubtopicRepository subtopicRepository;
    private final SubtopicProgressRepository progressRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Transactional
    public Map<String, Object> enroll(String courseId) {
        User user = getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (enrollmentRepository.existsByUserAndCourseId(user, courseId)) {
            throw new DuplicateEnrollmentException("You are already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder().user(user).course(course).build();
        Enrollment saved = enrollmentRepository.save(enrollment);

        return Map.of(
                "enrollmentId", saved.getId(),
                "courseId", course.getId(),
                "courseTitle", course.getTitle(),
                "enrolledAt", saved.getEnrolledAt() != null ? saved.getEnrolledAt() : LocalDateTime.now()
        );
    }

    @Transactional
    public Map<String, Object> markSubtopicComplete(String subtopicId) {
        User user = getCurrentUser();
        Subtopic subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Subtopic not found"));

        String courseId = subtopic.getTopic().getCourse().getId();

        // Check if user is enrolled
        if (!enrollmentRepository.existsByUserAndCourseId(user, courseId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You must be enrolled in this course to mark subtopics as complete"
            );
        }

        // Idempotent: return existing progress or create new
        SubtopicProgress progress = progressRepository.findByUserAndSubtopicId(user, subtopicId)
                .orElseGet(() -> {
                    SubtopicProgress newProgress = SubtopicProgress.builder()
                            .user(user)
                            .subtopic(subtopic)
                            .completed(true)
                            .build();
                    return progressRepository.save(newProgress);
                });

        if (!progress.isCompleted()) {
            progress.setCompleted(true);
            progressRepository.save(progress);
        }

        return Map.of(
                "subtopicId", subtopic.getId(),
                "completed", true,
                "completedAt", progress.getCompletedAt() != null ? progress.getCompletedAt() : LocalDateTime.now()
        );
    }

    public ProgressResponse getProgress(Long enrollmentId) {
        User user = getCurrentUser();
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        if (!enrollment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied");
        }

        Course course = enrollment.getCourse();
        long totalSubtopics = subtopicRepository.countByTopicCourseId(course.getId());

        List<SubtopicProgress> completedList = progressRepository.findCompletedByCourse(user.getId(), course.getId());
        long completedCount = completedList.size();

        double percentage = totalSubtopics == 0 ? 0 : ((double) completedCount / totalSubtopics) * 100;

        List<ProgressResponse.CompletedItem> items = completedList.stream()
                .map(p -> ProgressResponse.CompletedItem.builder()
                        .subtopicId(p.getSubtopic().getId())
                        .subtopicTitle(p.getSubtopic().getTitle())
                        .completedAt(p.getCompletedAt())
                        .build())
                .collect(Collectors.toList());

        return ProgressResponse.builder()
                .enrollmentId(enrollment.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .totalSubtopics(totalSubtopics)
                .completedSubtopics(completedCount)
                .completionPercentage(Math.round(percentage * 100.0) / 100.0)
                .completedItems(items)
                .build();
    }
}