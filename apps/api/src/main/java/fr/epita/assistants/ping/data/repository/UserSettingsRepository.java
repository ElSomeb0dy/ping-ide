package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserSettingsModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UserSettingsRepository implements PanacheRepository<UserSettingsModel> {
  public UserSettingsModel findByUserId(UUID userId) {
    return find("userId", userId).firstResult();
  }
}
