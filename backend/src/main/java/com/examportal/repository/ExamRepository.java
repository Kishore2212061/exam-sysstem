package com.examportal.repository;

import com.examportal.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {
    List<Exam> findByStatus(Exam.ExamStatus status);
    List<Exam> findByCreatedBy(String adminId);
    List<Exam> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Exam> findByStatusIn(List<Exam.ExamStatus> statuses);
    long countByStatus(Exam.ExamStatus status);
    java.util.Optional<Exam> findByTitle(String title);
}
