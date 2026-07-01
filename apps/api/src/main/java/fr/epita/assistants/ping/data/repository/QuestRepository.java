package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.QuestModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class QuestRepository implements PanacheRepository<QuestModel> {
  public QuestModel findById(UUID id) {
    return find("id", id).firstResult();
  }

  public QuestModel findByCode(String code) {
    return find("code", code).firstResult();
  }
}
