package com.edhub.api.service;

import com.edhub.api.dto.response.CourseListResponse;
import com.edhub.api.entity.Course;
import com.edhub.api.exception.ResourceNotFoundException;
import com.edhub.api.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseListResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(c -> CourseListResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())

                        .topicCount(c.getTopics().size())
                        .subtopicCount(c.getTopics().stream().mapToInt(t -> t.getSubtopics().size()).sum())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Course getCourseById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id '" + id + "' does not exist"));


        course.getTopics().forEach(topic -> topic.getSubtopics().size());

        return course;
    }
}