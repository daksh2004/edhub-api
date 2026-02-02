package com.edhub.api.repository;

import com.edhub.api.entity.SubtopicProgress;
import com.edhub.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubtopicProgressRepository extends JpaRepository<SubtopicProgress, Long> {
    Optional<SubtopicProgress> findByUserAndSubtopicId(User user, String subtopicId);

    @Query("SELECT sp FROM SubtopicProgress sp WHERE sp.user.id = :userId AND sp.subtopic.topic.course.id = :courseId AND sp.completed = true")
    List<SubtopicProgress> findCompletedByCourse(@Param("userId") Long userId, @Param("courseId") String courseId);
}