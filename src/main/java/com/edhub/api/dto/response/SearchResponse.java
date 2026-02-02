package com.edhub.api.dto.response;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class SearchResponse {
    private String query;
    private List<SearchResult> results;

    @Data @Builder
    public static class SearchResult {
        private String courseId;
        private String courseTitle;
        private List<Match> matches;
    }

    @Data @Builder
    public static class Match {
        private String type;
        private String topicTitle;
        private String subtopicId;
        private String subtopicTitle;
        private String snippet;
    }
}