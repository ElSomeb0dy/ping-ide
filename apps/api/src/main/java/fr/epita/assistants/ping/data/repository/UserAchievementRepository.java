package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserAchievementModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserAchievementRepository implements PanacheRepository<UserAchievementModel> {
  public UserAchievementModel findByUserAndAchievement(UUID userId, UUID achievementId) {
    return find("userId = ?1 AND achievementId = ?2", userId, achievementId).firstResult();
  }

  public List<UserAchievementModel> findByUser(UUID userId) {
    return list("userId", userId);
  }
}
