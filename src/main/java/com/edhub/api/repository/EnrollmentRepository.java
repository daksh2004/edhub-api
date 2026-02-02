package com.edhub.api.repository;

import com.edhub.api.entity.Enrollment;
import com.edhub.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserAndCourseId(User user, String courseId);
    Optional<Enrollment> findByUserAndCourseId(User user, String courseId);
}