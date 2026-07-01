package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.converter.LessonConverter;
import fr.epita.assistants.ping.data.model.ExerciseModel;
import fr.epita.assistants.ping.data.model.LessonModel;
import fr.epita.assistants.ping.data.model.UserExerciseProgressModel;
import fr.epita.assistants.ping.data.model.UserLessonProgressModel;
import fr.epita.assistants.ping.data.repository.ExerciseRepository;
import fr.epita.assistants.ping.data.repository.LessonRepository;
import fr.epita.assistants.ping.data.repository.UserExerciseProgressRepository;
import fr.epita.assistants.ping.data.repository.UserLessonProgressRepository;
import fr.epita.assistants.ping.presentation.api.response.ExerciseResponse;
import fr.epita.assistants.ping.presentation.api.response.LessonResponse;
import fr.epita.assistants.ping.utils.HttpError;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/api/lessons")
@Produces(MediaType.APPLICATION_JSON)
public class LessonResource {
  @Inject
  LessonRepository lessonRepository;
  @Inject
  ExerciseRepository exerciseRepository;
  @Inject
  UserExerciseProgressRepository userExerciseProgressRepository;
  @Inject
  UserLessonProgressRepository userLessonProgressRepository;
  @Inject
  LessonConverter lessonConverter;
  @Inject
  JsonWebToken jwt;

  @GET
  @Authenticated
  public Response fetchLessons() {
    UUID userId = UUID.fromString(jwt.getSubject());
    List<LessonResponse> responses = new ArrayList<>();
    for (LessonModel lesson : lessonRepository.fetchAllOrdered()) {
      responses.add(toProgressResponse(userId, lesson, false));
    }
    return Response.ok(responses).build();
  }

  @GET
  @Path("/{id}")
  @Authenticated
  public Response fetchLesson(@PathParam("id") UUID id) {
    LessonModel lesson = lessonRepository.findById(id);
    if (lesson == null) {
      throw new HttpError(Response.Status.NOT_FOUND, "Lesson not found").get();
    }

    return Response.ok(toProgressResponse(UUID.fromString(jwt.getSubject()), lesson, true)).build();
  }

  private LessonResponse toProgressResponse(UUID userId, LessonModel lesson, Boolean includeExercises) {
    List<ExerciseModel> exercises = exerciseRepository.findByLessonId(lesson.getId());
    UserLessonProgressModel lessonProgress = userLessonProgressRepository.findByUserAndLesson(userId, lesson.getId());
    Integer completed = lessonProgress == null ? 0 : lessonProgress.getCompletedExercises();
    Integer total = exercises.size();
    Integer percentage = total == 0 ? 0 : Math.round((completed * 100f) / total);
    String status = lessonStatus(userId, lesson, total, completed);
    List<ExerciseResponse> exerciseResponses = includeExercises ? exerciseResponses(userId, exercises, status) : List.of();

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
        completed,
        total,
        percentage,
        exerciseResponses);
  }

  private List<ExerciseResponse> exerciseResponses(UUID userId, List<ExerciseModel> exercises, String lessonStatus) {
    List<ExerciseResponse> responses = new ArrayList<>();
    boolean previousCompleted = true;

    for (ExerciseModel exercise : exercises) {
      UserExerciseProgressModel progress = userExerciseProgressRepository.findByUserAndExercise(userId, exercise.getId());
      boolean completed = progress != null && "COMPLETED".equals(progress.getStatus());
      String status = completed ? "COMPLETED" : ("LOCKED".equals(lessonStatus) || !previousCompleted ? "LOCKED" : "AVAILABLE");
      responses.add(new ExerciseResponse(
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
          status));
      previousCompleted = completed;
    }

    return responses;
  }

  private String lessonStatus(UUID userId, LessonModel lesson, Integer total, Integer completed) {
    if (total > 0 && completed.equals(total)) {
      return "COMPLETED";
    }

    UUID prerequisiteId = lesson.getPrerequisiteLessonId();
    if (prerequisiteId == null) {
      return "IN_PROGRESS";
    }

    UserLessonProgressModel prerequisite = userLessonProgressRepository.findByUserAndLesson(userId, prerequisiteId);
    if (prerequisite != null && "COMPLETED".equals(prerequisite.getStatus())) {
      return "IN_PROGRESS";
    }

    return "LOCKED";
  }
}
