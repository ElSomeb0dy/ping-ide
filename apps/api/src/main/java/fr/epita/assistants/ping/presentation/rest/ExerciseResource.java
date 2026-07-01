package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.converter.ExerciseConverter;
import fr.epita.assistants.ping.data.model.ExerciseModel;
import fr.epita.assistants.ping.data.model.SubmissionModel;
import fr.epita.assistants.ping.data.model.TestCaseModel;
import fr.epita.assistants.ping.data.model.UserExerciseProgressModel;
import fr.epita.assistants.ping.data.repository.ExerciseRepository;
import fr.epita.assistants.ping.data.repository.SubmissionRepository;
import fr.epita.assistants.ping.data.repository.TestCaseRepository;
import fr.epita.assistants.ping.data.repository.UserExerciseProgressRepository;
import fr.epita.assistants.ping.domain.service.CodeExecutionService;
import fr.epita.assistants.ping.domain.service.AchievementService;
import fr.epita.assistants.ping.domain.service.GamificationService;
import fr.epita.assistants.ping.domain.service.ProgressService;
import fr.epita.assistants.ping.presentation.api.response.AchievementResponse;
import fr.epita.assistants.ping.presentation.api.request.SubmitExerciseRequest;
import fr.epita.assistants.ping.presentation.api.response.SubmitExerciseResponse;
import fr.epita.assistants.ping.utils.HttpError;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
@Path("/api/exercises")
@Produces(MediaType.APPLICATION_JSON)
public class ExerciseResource {
  @Inject
  ExerciseRepository exerciseRepository;
  @Inject
  TestCaseRepository testCaseRepository;
  @Inject
  SubmissionRepository submissionRepository;
  @Inject
  UserExerciseProgressRepository userExerciseProgressRepository;
  @Inject
  ExerciseConverter exerciseConverter;
  @Inject
  CodeExecutionService codeExecutionService;
  @Inject
  AchievementService achievementService;
  @Inject
  ProgressService progressService;
  @Inject
  GamificationService gamificationService;
  @Inject
  JsonWebToken jwt;

  @GET
  @Path("/{id}")
  @Authenticated
  public Response fetchExercise(@PathParam("id") UUID id) {
    ExerciseModel exercise = exerciseRepository.findById(id);
    if (exercise == null) {
      throw new HttpError(Response.Status.NOT_FOUND, "Exercise not found").get();
    }
    return Response.ok(exerciseConverter.toResponse(exercise)).build();
  }

  @POST
  @Path("/{id}/submit")
  @Authenticated
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response submitExercise(@PathParam("id") UUID id, SubmitExerciseRequest request) {
    if (request == null || request.language == null || request.code == null) {
      throw new HttpError(Response.Status.BAD_REQUEST, "language and code are required").get();
    }

    ExerciseModel exercise = exerciseRepository.findById(id);
    if (exercise == null) {
      throw new HttpError(Response.Status.NOT_FOUND, "Exercise not found").get();
    }

    if (exercise.getAllowedLanguages() == null || !exercise.getAllowedLanguages().contains("\"" + request.language + "\"")) {
      throw new HttpError(Response.Status.BAD_REQUEST, "Language is not allowed for this exercise").get();
    }

    List<TestCaseModel> testCases = testCaseRepository.findByExerciseId(id);
    if (testCases.isEmpty()) {
      throw new HttpError(Response.Status.BAD_REQUEST, "Exercise has no test cases").get();
    }

    List<SubmitExerciseResponse.TestResultResponse> testResults = new ArrayList<>();
    boolean passed = true;

    for (TestCaseModel testCase : testCases) {
      CodeExecutionService.ExecutionResult result = codeExecutionService.run(request.language, request.code, testCase.getInput());
      String actual = result.stdout() == null ? "" : result.stdout().trim();
      String expected = testCase.getExpectedOutput() == null ? "" : testCase.getExpectedOutput().trim();
      boolean testPassed = !Boolean.TRUE.equals(result.timedOut()) && result.exitCode() == 0 && actual.equals(expected);
      passed = passed && testPassed;
      testResults.add(new SubmitExerciseResponse.TestResultResponse(
          Boolean.TRUE.equals(testCase.getIsHidden()) ? "Test caché" : testCase.getInput(),
          Boolean.TRUE.equals(testCase.getIsHidden()) ? "Test caché" : expected,
          actual.isBlank() ? result.stderr() : actual,
          testPassed));
    }

    UUID userId = UUID.fromString(jwt.getSubject());
    UserExerciseProgressModel existingProgress = userExerciseProgressRepository.findByUserAndExercise(userId, id);
    boolean alreadyCompleted = existingProgress != null && "COMPLETED".equals(existingProgress.getStatus());
    int xpAwarded = passed && !alreadyCompleted ? exercise.getXpReward() : 0;

    SubmissionModel submission = new SubmissionModel();
    submission.setUserId(userId);
    submission.setExerciseId(id);
    submission.setLanguage(request.language);
    submission.setCode(request.code);
    submission.setStatus(passed ? "PASSED" : "FAILED");
    submission.setResultsJson(resultsToJson(testResults));
    submission.setXpAwarded(xpAwarded);
    submissionRepository.persist(submission);

    GamificationService.AwardResult awardResult = new GamificationService.AwardResult(0, false, null);
    List<AchievementResponse> newAchievements = List.of();
    if (passed) {
      if (xpAwarded > 0) {
        awardResult = gamificationService.awardXp(userId, xpAwarded);
      }
      progressService.markExerciseCompleted(userId, exercise, submission.getId(), xpAwarded);
      newAchievements = achievementService.checkAndUnlock(userId);
    }

    return Response.ok(new SubmitExerciseResponse(
        passed ? "PASSED" : "FAILED",
        testResults,
        xpAwarded,
        awardResult.leveledUp(),
        awardResult.newLevel(),
        newAchievements,
        List.of())).build();
  }

  private String resultsToJson(List<SubmitExerciseResponse.TestResultResponse> results) {
    StringBuilder builder = new StringBuilder("[");
    for (int i = 0; i < results.size(); i++) {
      SubmitExerciseResponse.TestResultResponse result = results.get(i);
      if (i > 0) {
        builder.append(",");
      }
      builder
          .append("{\"input\":\"").append(escape(result.input)).append("\",")
          .append("\"expected\":\"").append(escape(result.expected)).append("\",")
          .append("\"actual\":\"").append(escape(result.actual)).append("\",")
          .append("\"passed\":").append(result.passed).append("}");
    }
    return builder.append("]").toString();
  }

  private String escape(String value) {
    if (value == null) {
      return "";
    }
    return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
  }
}
