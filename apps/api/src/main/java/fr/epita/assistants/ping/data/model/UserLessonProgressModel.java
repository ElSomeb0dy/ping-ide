package fr.epita.assistants.ping.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_lesson_progress")
@Getter
@Setter
@NoArgsConstructor
public class UserLessonProgressModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "lesson_id", nullable = false)
  private UUID lessonId;

  @Column(name = "completed_exercises", nullable = false)
  private Integer completedExercises = 0;

  @Column(name = "total_exercises", nullable = false)
  private Integer totalExercises = 0;

  @Column(name = "percentage", nullable = false)
  private Integer percentage = 0;

  @Column(name = "status", nullable = false)
  private String status = "LOCKED";
}
