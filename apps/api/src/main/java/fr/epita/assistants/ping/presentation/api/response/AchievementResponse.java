package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
public class AchievementResponse {
  public UUID id;
  public String code;
  public String title;
  public String description;
  public String icon;
  public String criteriaType;
  public Integer criteriaValue;
  public Integer xpReward;
  public Boolean unlocked;
  public Instant unlockedAt;
}
