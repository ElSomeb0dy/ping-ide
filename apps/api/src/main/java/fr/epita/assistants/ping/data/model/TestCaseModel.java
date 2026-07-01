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
@Table(name = "test_cases")
@Getter
@Setter
@NoArgsConstructor
public class TestCaseModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "exercise_id", nullable = false)
  private UUID exerciseId;

  @Column(name = "input", columnDefinition = "TEXT")
  private String input;

  @Column(name = "expected_output", columnDefinition = "TEXT")
  private String expectedOutput;

  @Column(name = "is_hidden", nullable = false)
  private Boolean isHidden = false;

  @Column(name = "display_order", nullable = false)
  private Integer sortOrder;
}
