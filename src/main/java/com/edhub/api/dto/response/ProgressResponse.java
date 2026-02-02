package com.edhub.api.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class ProgressResponse {
    private Long enrollmentId;
    private String courseId;
    private String courseTitle;
    private long totalSubtopics;
    private long completedSubtopics;
    private double completionPercentage;
    private List<CompletedItem> completedItems;

    @Data @Builder
    public static class CompletedItem {
        private String subtopicId;
        private String subtopicTitle;
        private LocalDateTime completedAt;
    }
}