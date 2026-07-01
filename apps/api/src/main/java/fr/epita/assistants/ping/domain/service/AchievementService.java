package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.converter.AchievementConverter;
import fr.epita.assistants.ping.data.model.AchievementModel;
import fr.epita.assistants.ping.data.model.UserAchievementModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.AchievementRepository;
import fr.epita.assistants.ping.data.repository.UserAchievementRepository;
import fr.epita.assistants.ping.data.repository.UserExerciseProgressRepository;
import fr.epita.assistants.ping.data.repository.UserLessonProgressRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.presentation.api.response.AchievementResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AchievementService {
  @Inject
  AchievementRepository achievementRepository;
  @Inject
  UserAchievementRepository userAchievementRepository;
  @Inject
  UserExerciseProgressRepository userExerciseProgressRepository;
  @Inject
  UserLessonProgressRepository userLessonProgressRepository;
  @Inject
  UserRepository userRepository;
  @Inject
  AchievementConverter achievementConverter;

  @Transactional
  public List<AchievementResponse> checkAndUnlock(UUID userId) {
    List<AchievementResponse> unlockedNow = new ArrayList<>();
    UserModel user = userRepository.findById(userId);
    if (user == null) {
      return unlockedNow;
    }

    long completedExercises = userExerciseProgressRepository.count("userId = ?1 AND status = ?2", userId, "COMPLETED");
    long completedLessons = userLessonProgressRepository.count("userId = ?1 AND status = ?2", userId, "COMPLETED");

    for (AchievementModel achievement : achievementRepository.listAll()) {
      if (userAchievementRepository.findByUserAndAchievement(userId, achievement.getId()) != null) {
        continue;
      }

      if (!criteriaMet(achievement, user, completedExercises, completedLessons)) {
        continue;
      }

      UserAchievementModel userAchievement = new UserAchievementModel();
      userAchievement.setUserId(userId);
      userAchievement.setAchievementId(achievement.getId());
      userAchievement.setUnlockedAt(Instant.now());
      userAchievementRepository.persist(userAchievement);
      unlockedNow.add(achievementConverter.toResponse(achievement, userAchievement));
    }

    return unlockedNow;
  }

  private Boolean criteriaMet(AchievementModel achievement, UserModel user, long completedExercises, long completedLessons) {
    Integer criteriaValue = achievement.getCriteriaValue() == null ? 0 : achievement.getCriteriaValue();
    return switch (achievement.getCriteriaType()) {
      case "EXERCISES_COMPLETED" -> completedExercises >= criteriaValue;
      case "LESSONS_COMPLETED" -> completedLessons >= criteriaValue;
      case "LEVEL" -> user.getLevel() != null && user.getLevel() >= criteriaValue;
      default -> false;
    };
  }
}
