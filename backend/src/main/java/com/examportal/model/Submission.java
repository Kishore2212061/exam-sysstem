package com.examportal.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String studentId;

    private String examId;

    @ElementCollection
    @CollectionTable(name = "submission_answers", joinColumns = @JoinColumn(name = "submission_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "answer", length = 2000)
    private Map<String, String> answers; // questionId -> answer

    @ElementCollection
    @CollectionTable(name = "submission_marked_for_review", joinColumns = @JoinColumn(name = "submission_id"))
    @Column(name = "question_id")
    private List<String> markedForReview;

    private Integer score;

    private Integer totalMarks;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.IN_PROGRESS;

    private int tabSwitchCount = 0;

    private boolean autoSubmitted = false;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @ElementCollection
    @CollectionTable(name = "submission_question_results", joinColumns = @JoinColumn(name = "submission_id"))
    private List<QuestionResult> questionResults;

    private String descriptiveEvaluation;

    private boolean resultPublished = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class QuestionResult {
        private String questionId;
        private String givenAnswer;
        private String correctAnswer;
        private boolean isCorrect;
        private int marksAwarded;
    }

    public enum SubmissionStatus {
        IN_PROGRESS, SUBMITTED, EVALUATED
    }
}
