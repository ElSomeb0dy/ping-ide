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

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_quests")
@Getter
@Setter
@NoArgsConstructor
public class UserQuestModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "quest_id", nullable = false)
  private UUID questId;

  @Column(name = "quest_date", nullable = false)
  private LocalDate date;

  @Column(name = "progress_current", nullable = false)
  private Integer progressCurrent = 0;

  @Column(name = "progress_target", nullable = false)
  private Integer progressTarget = 1;

  @Column(name = "status", nullable = false)
  private String status = "IN_PROGRESS";
}
