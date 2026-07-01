package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.SubmissionModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SubmissionRepository implements PanacheRepository<SubmissionModel> {
  public SubmissionModel findById(UUID id) {
    return find("id", id).firstResult();
  }

  public List<SubmissionModel> findByUserAndExercise(UUID userId, UUID exerciseId) {
    return list("userId = ?1 AND exerciseId = ?2 ORDER BY createdAt DESC", userId, exerciseId);
  }
}
