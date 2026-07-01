package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class LessonResponse {
  public UUID id;
  public String slug;
  public String title;
  public String description;
  public Integer order;
  public String difficulty;
  public String icon;
  public UUID prerequisiteLessonId;
  public String status;
  public Integer completedExercises;
  public Integer totalExercises;
  public Integer percentage;
  public List<ExerciseResponse> exercises;
}
