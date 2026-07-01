package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.utils.HttpError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class GamificationService {
  @Inject
  UserRepository userRepository;

  public Integer levelForXp(Integer xp) {
    int safeXp = Math.max(xp == null ? 0 : xp, 0);
    return (int) Math.floor(Math.sqrt(safeXp / 50.0)) + 1;
  }

  public Integer xpForNextLevel(Integer level) {
    int nextLevel = Math.max(level == null ? 1 : level, 1) + 1;
    return (nextLevel - 1) * (nextLevel - 1) * 50;
  }

  @Transactional
  public AwardResult awardXp(UUID userId, Integer xp) {
    UserModel user = userRepository.findById(userId);
    if (user == null) {
      throw new HttpError(Response.Status.NOT_FOUND, "User not found").get();
    }

    Integer oldLevel = user.getLevel();
    int nextXp = Math.max(user.getXp() == null ? 0 : user.getXp(), 0) + Math.max(xp == null ? 0 : xp, 0);

    user.setXp(nextXp);
    user.setLevel(levelForXp(nextXp));
    touchStreak(user);

    return new AwardResult(xp, !user.getLevel().equals(oldLevel), user.getLevel());
  }

  @Transactional
  public void touchActivity(UUID userId) {
    UserModel user = userRepository.findById(userId);
    if (user == null) {
      throw new HttpError(Response.Status.NOT_FOUND, "User not found").get();
    }
    touchStreak(user);
  }

  private void touchStreak(UserModel user) {
    LocalDate today = LocalDate.now();
    LocalDate lastActivity = user.getLastActivityDate();

    if (today.equals(lastActivity)) {
      return;
    }

    int currentStreak = user.getCurrentStreak() == null ? 0 : user.getCurrentStreak();
    if (lastActivity != null && lastActivity.plusDays(1).equals(today)) {
      currentStreak += 1;
    } else {
      currentStreak = 1;
    }

    user.setCurrentStreak(currentStreak);
    user.setLongestStreak(Math.max(user.getLongestStreak() == null ? 0 : user.getLongestStreak(), currentStreak));
    user.setLastActivityDate(today);
  }

  public record AwardResult(Integer xpAwarded, Boolean leveledUp, Integer newLevel) {
  }
}
