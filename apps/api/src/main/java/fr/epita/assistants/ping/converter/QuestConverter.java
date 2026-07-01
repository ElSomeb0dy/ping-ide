package fr.epita.assistants.ping.converter;

import fr.epita.assistants.ping.data.model.QuestModel;
import fr.epita.assistants.ping.data.model.UserQuestModel;
import fr.epita.assistants.ping.presentation.api.response.QuestResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuestConverter {
  public QuestResponse toResponse(QuestModel quest, UserQuestModel userQuest) {
    return new QuestResponse(
        quest.getId(),
        quest.getCode(),
        quest.getTitle(),
        quest.getDescription(),
        quest.getXpReward(),
        quest.getType(),
        quest.getCriteria(),
        userQuest == null ? null : userQuest.getDate(),
        userQuest == null ? 0 : userQuest.getProgressCurrent(),
        userQuest == null ? quest.getCriteria() : userQuest.getProgressTarget(),
        userQuest == null ? "AVAILABLE" : userQuest.getStatus());
  }
}
