package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.ExerciseModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ExerciseRepository implements PanacheRepository<ExerciseModel> {
  public ExerciseModel findById(UUID id) {
    return find("id", id).firstResult();
  }

  public List<ExerciseModel> findByLessonId(UUID lessonId) {
    return list("lessonId = ?1 ORDER BY sortOrder ASC", lessonId);
  }
}
