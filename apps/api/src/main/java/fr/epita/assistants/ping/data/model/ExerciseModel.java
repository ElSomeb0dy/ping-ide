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
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
public class ExerciseModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "lesson_id", nullable = false)
  private UUID lessonId;

  @Column(name = "slug", nullable = false)
  private String slug;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "statement_md", columnDefinition = "TEXT")
  private String statementMd;

  @Column(name = "examples_json", columnDefinition = "TEXT")
  private String examplesJson;

  @Column(name = "difficulty", nullable = false)
  private String difficulty;

  @Column(name = "xp_reward", nullable = false)
  private Integer xpReward;

  @Column(name = "display_order", nullable = false)
  private Integer sortOrder;

  @Column(name = "allowed_languages", columnDefinition = "TEXT")
  private String allowedLanguages;

  @Column(name = "starter_code_json", columnDefinition = "TEXT")
  private String starterCodeJson;
}
