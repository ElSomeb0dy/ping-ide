package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.LessonModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LessonRepository implements PanacheRepository<LessonModel> {
  public LessonModel findById(UUID id) {
    return find("id", id).firstResult();
  }

  public LessonModel findBySlug(String slug) {
    return find("slug", slug).firstResult();
  }

  public List<LessonModel> fetchAllOrdered() {
    return list("ORDER BY sortOrder ASC");
  }
}
