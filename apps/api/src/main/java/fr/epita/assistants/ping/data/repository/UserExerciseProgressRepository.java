package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserExerciseProgressModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserExerciseProgressRepository implements PanacheRepository<UserExerciseProgressModel> {
  public UserExerciseProgressModel findByUserAndExercise(UUID userId, UUID exerciseId) {
    return find("userId = ?1 AND exerciseId = ?2", userId, exerciseId).firstResult();
  }

  public List<UserExerciseProgressModel> findByUser(UUID userId) {
    return list("userId", userId);
  }
}
