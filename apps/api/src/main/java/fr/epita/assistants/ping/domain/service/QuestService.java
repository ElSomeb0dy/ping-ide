package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.converter.QuestConverter;
import fr.epita.assistants.ping.data.model.QuestModel;
import fr.epita.assistants.ping.data.model.UserQuestModel;
import fr.epita.assistants.ping.data.repository.QuestRepository;
import fr.epita.assistants.ping.data.repository.UserQuestRepository;
import fr.epita.assistants.ping.errors.ErrorsCode;
import fr.epita.assistants.ping.presentation.api.response.QuestResponse;
import fr.epita.assistants.ping.utils.HttpError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QuestService {
  @Inject
  QuestRepository questRepository;
  @Inject
  UserQuestRepository userQuestRepository;
  @Inject
  QuestConverter questConverter;
  @Inject
  GamificationService gamificationService;

  @Transactional
  public List<QuestResponse> fetchDailyQuests(UUID userId) {
    List<QuestResponse> responses = new ArrayList<>();
    for (QuestModel quest : questRepository.listAll()) {
      UserQuestModel userQuest = ensureDailyQuest(userId, quest);
      responses.add(questConverter.toResponse(quest, userQuest));
    }
    return responses;
  }

  @Transactional
  public List<QuestResponse> recordProgress(UUID userId, String type, Integer amount) {
    int increment = Math.max(amount == null ? 0 : amount, 0);
    List<QuestResponse> responses = new ArrayList<>();
    if (increment == 0) {
      return responses;
    }

    for (QuestModel quest : questRepository.listAll()) {
      if (!type.equals(quest.getType())) {
        continue;
      }

      UserQuestModel userQuest = ensureDailyQuest(userId, quest);
      if ("CLAIMED".equals(userQuest.getStatus())) {
        responses.add(questConverter.toResponse(quest, userQuest));
        continue;
      }

      int target = Math.max(userQuest.getProgressTarget() == null ? quest.getCriteria() : userQuest.getProgressTarget(), 1);
      int current = Math.min((userQuest.getProgressCurrent() == null ? 0 : userQuest.getProgressCurrent()) + increment, target);
      userQuest.setProgressCurrent(current);
      if (current >= target) {
        userQuest.setStatus("COMPLETED");
      }
      responses.add(questConverter.toResponse(quest, userQuest));
    }
    return responses;
  }

  @Transactional
  public QuestResponse claim(UUID userId, UUID questId) {
    QuestModel quest = questRepository.findById(questId);
    if (quest == null) {
      ErrorsCode.QUEST_NOT_FOUND.throwException();
    }

    UserQuestModel userQuest = ensureDailyQuest(userId, quest);
    if (!"COMPLETED".equals(userQuest.getStatus())) {
      throw new HttpError(Response.Status.BAD_REQUEST, "Quest is not ready to claim").get();
    }

    userQuest.setStatus("CLAIMED");
    gamificationService.awardXp(userId, quest.getXpReward());
    return questConverter.toResponse(quest, userQuest);
  }

  private UserQuestModel ensureDailyQuest(UUID userId, QuestModel quest) {
    LocalDate today = LocalDate.now();
    UserQuestModel userQuest = userQuestRepository.findByUserQuestAndDate(userId, quest.getId(), today);
    if (userQuest != null) {
      userQuest.setProgressTarget(quest.getCriteria());
      if ("LOGIN".equals(quest.getType()) && !"CLAIMED".equals(userQuest.getStatus())) {
        completeQuest(userQuest);
      }
      return userQuest;
    }

    userQuest = new UserQuestModel();
    userQuest.setUserId(userId);
    userQuest.setQuestId(quest.getId());
    userQuest.setDate(today);
    userQuest.setProgressTarget(quest.getCriteria());
    userQuest.setStatus("IN_PROGRESS");
    if ("LOGIN".equals(quest.getType())) {
      completeQuest(userQuest);
    }
    userQuestRepository.persist(userQuest);
    return userQuest;
  }

  private void completeQuest(UserQuestModel userQuest) {
    int target = Math.max(userQuest.getProgressTarget() == null ? 1 : userQuest.getProgressTarget(), 1);
    userQuest.setProgressCurrent(target);
    userQuest.setStatus("COMPLETED");
  }
}
