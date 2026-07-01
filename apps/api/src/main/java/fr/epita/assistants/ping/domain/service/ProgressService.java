package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.model.ExerciseModel;
import fr.epita.assistants.ping.data.model.UserExerciseProgressModel;
import fr.epita.assistants.ping.data.model.UserLessonProgressModel;
import fr.epita.assistants.ping.data.repository.ExerciseRepository;
import fr.epita.assistants.ping.data.repository.UserExerciseProgressRepository;
import fr.epita.assistants.ping.data.repository.UserLessonProgressRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProgressService {
  @Inject
  ExerciseRepository exerciseRepository;
  @Inject
  UserExerciseProgressRepository userExerciseProgressRepository;
  @Inject
  UserLessonProgressRepository userLessonProgressRepository;

  @Transactional
  public void markExerciseCompleted(UUID userId, ExerciseModel exercise, UUID submissionId, Integer xpEarned) {
    UserExerciseProgressModel progress = userExerciseProgressRepository.findByUserAndExercise(userId, exercise.getId());
    if (progress == null) {
      progress = new UserExerciseProgressModel();
      progress.setUserId(userId);
      progress.setExerciseId(exercise.getId());
      progress.setStatus("COMPLETED");
      userExerciseProgressRepository.persist(progress);
    }

    progress.setStatus("COMPLETED");
    progress.setBestSubmissionId(submissionId);
    progress.setXpEarned(Math.max(progress.getXpEarned() == null ? 0 : progress.getXpEarned(), xpEarned == null ? 0 : xpEarned));
    progress.setCompletedAt(Instant.now());

    syncLessonProgress(userId, exercise.getLessonId());
  }

  @Transactional
  public UserLessonProgressModel syncLessonProgress(UUID userId, UUID lessonId) {
    List<ExerciseModel> exercises = exerciseRepository.findByLessonId(lessonId);
    long completed = exercises.stream()
        .filter(ex -> {
          UserExerciseProgressModel progress = userExerciseProgressRepository.findByUserAndExercise(userId, ex.getId());
          return progress != null && "COMPLETED".equals(progress.getStatus());
        })
        .count();

    UserLessonProgressModel lessonProgress = userLessonProgressRepository.findByUserAndLesson(userId, lessonId);
    if (lessonProgress == null) {
      lessonProgress = new UserLessonProgressModel();
      lessonProgress.setUserId(userId);
      lessonProgress.setLessonId(lessonId);
      userLessonProgressRepository.persist(lessonProgress);
    }

    int total = exercises.size();
    lessonProgress.setCompletedExercises((int) completed);
    lessonProgress.setTotalExercises(total);
    lessonProgress.setPercentage(total == 0 ? 0 : Math.round((completed * 100f) / total));
    lessonProgress.setStatus(total > 0 && completed == total ? "COMPLETED" : "IN_PROGRESS");

    return lessonProgress;
  }
}
