package com.edhub.api.repository;

import com.edhub.api.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, String> {
    @Query("SELECT t FROM Topic t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Topic> searchTopics(@Param("query") String query);
}