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
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String text;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    private List<Option> options;

    private String correctAnswer; // For MCQ: option id; for MULTI: comma-separated ids

    @ElementCollection
    @CollectionTable(name = "question_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "correct_answer")
    private List<String> correctAnswers; // For MULTI-SELECT

    private int marks = 1;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String subject;

    private String topic;

    private String explanation; // Shown after result

    private boolean isInQuestionBank = false;

    private String createdBy; // Admin user ID

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Option {
        private String id;
        private String text;
    }

    public enum QuestionType {
        MCQ, MULTI_SELECT, DESCRIPTIVE
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
