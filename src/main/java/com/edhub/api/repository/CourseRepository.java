package com.edhub.api.repository;

import com.edhub.api.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {
    // Search specific method for partial text match on title or description
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> searchCourses(@Param("query") String query);
}