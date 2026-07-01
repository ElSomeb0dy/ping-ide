package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.TestCaseModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TestCaseRepository implements PanacheRepository<TestCaseModel> {
  public TestCaseModel findById(UUID id) {
    return find("id", id).firstResult();
  }

  public List<TestCaseModel> findByExerciseId(UUID exerciseId) {
    return list("exerciseId = ?1 ORDER BY sortOrder ASC", exerciseId);
  }
}
