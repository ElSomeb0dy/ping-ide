package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserStatsResponse {
  public Integer level;
  public Integer xp;
  public Integer xpToNextLevel;
  public Integer currentStreak;
  public Integer longestStreak;
  public Long lessonsCompleted;
  public Long exercisesSolved;
  public Long achievementsUnlocked;
}
