package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.AchievementModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class AchievementRepository implements PanacheRepository<AchievementModel> {
  public AchievementModel findById(UUID id) {
    return find("id", id).firstResult();
  }

  public AchievementModel findByCode(String code) {
    return find("code", code).firstResult();
  }
}
