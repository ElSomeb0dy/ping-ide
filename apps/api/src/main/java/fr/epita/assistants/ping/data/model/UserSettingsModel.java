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
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
public class UserSettingsModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", unique = true, nullable = false)
  private UUID userId;

  @Column(name = "theme", nullable = false)
  private String theme = "Ping Dark";

  @Column(name = "default_language", nullable = false)
  private String defaultLanguage = "Python";

  @Column(name = "notifications_enabled", nullable = false)
  private Boolean notificationsEnabled = true;

  @Column(name = "sound_enabled", nullable = false)
  private Boolean soundEnabled = true;
}
