package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserQuestModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserQuestRepository implements PanacheRepository<UserQuestModel> {
  public UserQuestModel findByUserQuestAndDate(UUID userId, UUID questId, LocalDate date) {
    return find("userId = ?1 AND questId = ?2 AND date = ?3", userId, questId, date).firstResult();
  }

  public List<UserQuestModel> findByUserAndDate(UUID userId, LocalDate date) {
    return list("userId = ?1 AND date = ?2", userId, date);
  }
}
