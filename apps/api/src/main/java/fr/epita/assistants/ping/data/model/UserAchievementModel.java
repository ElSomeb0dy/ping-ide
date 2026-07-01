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
@Table(name = "user_achievements")
@Getter
@Setter
@NoArgsConstructor
public class UserAchievementModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "achievement_id", nullable = false)
  private UUID achievementId;

  @Column(name = "unlocked_at", nullable = false)
  private Instant unlockedAt = Instant.now();
}
