package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class ExerciseResponse {
  public UUID id;
  public UUID lessonId;
  public String slug;
  public String title;
  public String statementMd;
  public String examplesJson;
  public String difficulty;
  public Integer xpReward;
  public Integer order;
  public String allowedLanguages;
  public String starterCodeJson;
  public String status;
}
