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
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
public class AchievementModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "code", unique = true, nullable = false)
  private String code;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "icon")
  private String icon;

  @Column(name = "criteria_type", nullable = false)
  private String criteriaType;

  @Column(name = "criteria_value", nullable = false)
  private Integer criteriaValue;

  @Column(name = "xp_reward", nullable = false)
  private Integer xpReward = 0;
}
