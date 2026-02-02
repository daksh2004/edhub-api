package com.edhub.api.dto.response;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CourseListResponse {
    private String id;
    private String title;
    private String description;
    private int topicCount;
    private int subtopicCount;
}