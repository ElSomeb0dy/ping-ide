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

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_exercise_progress")
@Getter
@Setter
@NoArgsConstructor
public class UserExerciseProgressModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "exercise_id", nullable = false)
  private UUID exerciseId;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "best_submission_id")
  private UUID bestSubmissionId;

  @Column(name = "xp_earned", nullable = false)
  private Integer xpEarned = 0;

  @Column(name = "completed_at")
  private Instant completedAt;
}
