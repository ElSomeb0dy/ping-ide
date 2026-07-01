package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SubmitExerciseResponse {
  public String status;
  public List<TestResultResponse> testResults;
  public Integer xpAwarded;
  public Boolean leveledUp;
  public Integer newLevel;
  public List<AchievementResponse> newAchievements;
  public List<QuestResponse> questUpdates;

  @AllArgsConstructor
  public static class TestResultResponse {
    public String input;
    public String expected;
    public String actual;
    public Boolean passed;
  }
}
