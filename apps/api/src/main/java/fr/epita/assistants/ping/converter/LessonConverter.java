package fr.epita.assistants.ping.converter;

import fr.epita.assistants.ping.data.model.ExerciseModel;
import fr.epita.assistants.ping.data.model.LessonModel;
import fr.epita.assistants.ping.presentation.api.response.LessonResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LessonConverter {
  @Inject
  ExerciseConverter exerciseConverter;

  public LessonResponse toResponse(LessonModel lesson, List<ExerciseModel> exercises) {
    return toResponse(lesson, exercises, "LOCKED", 0, exercises.size(), 0);
  }

  public LessonResponse toResponse(
      LessonModel lesson,
      List<ExerciseModel> exercises,
      String status,
      Integer completedExercises,
      Integer totalExercises,
      Integer percentage) {
    return new LessonResponse(
        lesson.getId(),
        lesson.getSlug(),
        lesson.getTitle(),
        lesson.getDescription(),
        lesson.getSortOrder(),
        lesson.getDifficulty(),
        lesson.getIcon(),
        lesson.getPrerequisiteLessonId(),
        status,
        completedExercises,
        totalExercises,
        percentage,
        exerciseConverter.toResponseList(exercises));
  }

  public List<LessonResponse> toResponseList(List<LessonModel> lessons) {
    List<LessonResponse> responses = new ArrayList<>();
    for (LessonModel lesson : lessons) {
      responses.add(toResponse(lesson, List.of()));
    }
    return responses;
  }
}
