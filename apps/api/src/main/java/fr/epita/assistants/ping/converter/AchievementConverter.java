package fr.epita.assistants.ping.converter;

import fr.epita.assistants.ping.data.model.AchievementModel;
import fr.epita.assistants.ping.data.model.UserAchievementModel;
import fr.epita.assistants.ping.presentation.api.response.AchievementResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AchievementConverter {
  public AchievementResponse toResponse(AchievementModel achievement, UserAchievementModel unlocked) {
    return new AchievementResponse(
        achievement.getId(),
        achievement.getCode(),
        achievement.getTitle(),
        achievement.getDescription(),
        achievement.getIcon(),
        achievement.getCriteriaType(),
        achievement.getCriteriaValue(),
        achievement.getXpReward(),
        unlocked != null,
        unlocked == null ? null : unlocked.getUnlockedAt());
  }

  public List<AchievementResponse> toResponseList(List<AchievementModel> achievements) {
    List<AchievementResponse> responses = new ArrayList<>();
    for (AchievementModel achievement : achievements) {
      responses.add(toResponse(achievement, null));
    }
    return responses;
  }
}
