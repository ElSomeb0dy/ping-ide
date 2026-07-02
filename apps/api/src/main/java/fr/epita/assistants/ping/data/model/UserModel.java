package fr.epita.assistants.ping.data.model;

import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserModel {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "login", unique = true, nullable = false)
  private String login;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "is_admin", nullable = false)
  private Boolean isAdmin;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "avatar")
  private String avatar;

  @Column(name = "xp", nullable = false)
  private Integer xp = 0;

  @Column(name = "level", nullable = false)
  private Integer level = 1;

  @Column(name = "current_streak", nullable = false)
  private Integer currentStreak = 0;

  @Column(name = "longest_streak", nullable = false)
  private Integer longestStreak = 0;

  @Column(name = "last_activity_date")
  private LocalDate lastActivityDate;
}
