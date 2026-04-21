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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String description;

    private int durationMinutes;

    private int totalMarks;

    private int passingMarks;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ExamStatus status = ExamStatus.DRAFT;

    @ElementCollection
    @CollectionTable(name = "exam_question_ids", joinColumns = @JoinColumn(name = "exam_id"))
    @Column(name = "question_id")
    private List<String> questionIds;

    private int maxAttempts = 1;

    private boolean shuffleQuestions = true;

    private boolean shuffleOptions = true;

    private String subject;

    private String createdBy; // Admin user ID

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ExamStatus {
        DRAFT, SCHEDULED, ACTIVE, COMPLETED, CANCELLED
    }
}
