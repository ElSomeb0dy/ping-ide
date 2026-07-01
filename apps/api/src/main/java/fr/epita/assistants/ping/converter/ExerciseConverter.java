package fr.epita.assistants.ping.converter;

import fr.epita.assistants.ping.data.model.ExerciseModel;
import fr.epita.assistants.ping.presentation.api.response.ExerciseResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ExerciseConverter {
  public ExerciseResponse toResponse(ExerciseModel exercise) {
    return toResponse(exercise, "LOCKED");
  }

  public ExerciseResponse toResponse(ExerciseModel exercise, String status) {
    return new ExerciseResponse(
        exercise.getId(),
        exercise.getLessonId(),
        exercise.getSlug(),
        exercise.getTitle(),
        exercise.getStatementMd(),
        exercise.getExamplesJson(),
        exercise.getDifficulty(),
        exercise.getXpReward(),
        exercise.getSortOrder(),
        exercise.getAllowedLanguages(),
        exercise.getStarterCodeJson(),
        status);
  }

  public List<ExerciseResponse> toResponseList(List<ExerciseModel> exercises) {
    List<ExerciseResponse> responses = new ArrayList<>();
    for (ExerciseModel exercise : exercises) {
      responses.add(toResponse(exercise));
    }
    return responses;
  }
}
