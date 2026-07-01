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
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
public class SubmissionModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "exercise_id", nullable = false)
  private UUID exerciseId;

  @Column(name = "language", nullable = false)
  private String language;

  @Column(name = "code", columnDefinition = "TEXT", nullable = false)
  private String code;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "results_json", columnDefinition = "TEXT")
  private String resultsJson;

  @Column(name = "xp_awarded", nullable = false)
  private Integer xpAwarded = 0;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}
