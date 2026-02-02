package com.edhub.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subtopics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subtopic {
    @Id
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @JsonIgnore
    private Topic topic;
}