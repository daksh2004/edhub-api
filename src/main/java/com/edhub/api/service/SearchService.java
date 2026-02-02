package com.edhub.api.service;

import com.edhub.api.dto.response.SearchResponse;
import com.edhub.api.entity.Course;
import com.edhub.api.entity.Subtopic;
import com.edhub.api.entity.Topic;
import com.edhub.api.repository.CourseRepository;
import com.edhub.api.repository.SubtopicRepository;
import com.edhub.api.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final SubtopicRepository subtopicRepository;

    public SearchResponse search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return SearchResponse.builder().query(query).results(Collections.emptyList()).build();
        }

        String lowerQuery = query.toLowerCase();
        Map<String, SearchResponse.SearchResult> resultMap = new HashMap<>();

        // 1. Search Courses
        List<Course> courses = courseRepository.searchCourses(lowerQuery);
        for (Course c : courses) {
            addMatch(resultMap, c, "course", null, null, null, null);
        }

        // 2. Search Topics
        List<Topic> topics = topicRepository.searchTopics(lowerQuery);
        for (Topic t : topics) {
            addMatch(resultMap, t.getCourse(), "topic", t.getTitle(), null, null, null);
        }

        // 3. Search Subtopics (Title & Content)
        List<Subtopic> subtopics = subtopicRepository.searchSubtopics(lowerQuery);
        for (Subtopic s : subtopics) {
            String type = s.getTitle().toLowerCase().contains(lowerQuery) ? "subtopic" : "content";
            String snippet = getSnippet(s.getContent(), lowerQuery);
            addMatch(resultMap, s.getTopic().getCourse(), type, s.getTopic().getTitle(), s.getId(), s.getTitle(), snippet);
        }

        return SearchResponse.builder()
                .query(query)
                .results(new ArrayList<>(resultMap.values()))
                .build();
    }

    private void addMatch(Map<String, SearchResponse.SearchResult> map, Course course, String type,
                          String topicTitle, String subtopicId, String subtopicTitle, String snippet) {
        map.computeIfAbsent(course.getId(), k -> SearchResponse.SearchResult.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .matches(new ArrayList<>())
                .build());

        map.get(course.getId()).getMatches().add(SearchResponse.Match.builder()
                .type(type)
                .topicTitle(topicTitle)
                .subtopicId(subtopicId)
                .subtopicTitle(subtopicTitle)
                .snippet(snippet)
                .build());
    }

    private String getSnippet(String content, String query) {
        if (content == null) return null;
        int index = content.toLowerCase().indexOf(query);
        if (index == -1) return null;

        int start = Math.max(0, index - 30);
        int end = Math.min(content.length(), index + query.length() + 30);
        return (start > 0 ? "..." : "") + content.substring(start, end) + (end < content.length() ? "..." : "");
    }
}