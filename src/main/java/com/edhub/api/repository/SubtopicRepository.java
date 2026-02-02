package com.edhub.api.repository;

import com.edhub.api.entity.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubtopicRepository extends JpaRepository<Subtopic, String> {
    @Query("SELECT s FROM Subtopic s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Subtopic> searchSubtopics(@Param("query") String query);

    long countByTopicCourseId(String courseId);
}