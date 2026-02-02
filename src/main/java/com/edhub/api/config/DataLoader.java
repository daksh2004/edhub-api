package com.edhub.api.config;

import com.edhub.api.entity.Course;
import com.edhub.api.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            try {
                InputStream inputStream = TypeReference.class.getResourceAsStream("/seed-data.json");
                List<Course> courses = objectMapper.readValue(inputStream, new TypeReference<List<Course>>(){});

                // Establish relationships for JPA to save correctly (since JSON is nested)
                courses.forEach(course -> {
                    if (course.getTopics() != null) {
                        course.getTopics().forEach(topic -> {
                            topic.setCourse(course);
                            if (topic.getSubtopics() != null) {
                                topic.getSubtopics().forEach(subtopic -> subtopic.setTopic(topic));
                            }
                        });
                    }
                });

                courseRepository.saveAll(courses);
                log.info("Seed data loaded successfully: {} courses.", courses.size());
            } catch (Exception e) {
                log.error("Failed to load seed data: {}", e.getMessage());
            }
        } else {
            log.info("Database already contains data. Skipping seed.");
        }
    }
}