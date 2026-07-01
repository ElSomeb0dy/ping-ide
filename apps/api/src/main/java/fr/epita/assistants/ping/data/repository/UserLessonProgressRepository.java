package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserLessonProgressModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserLessonProgressRepository implements PanacheRepository<UserLessonProgressModel> {
  public UserLessonProgressModel findByUserAndLesson(UUID userId, UUID lessonId) {
    return find("userId = ?1 AND lessonId = ?2", userId, lessonId).firstResult();
  }

  public List<UserLessonProgressModel> findByUser(UUID userId) {
    return list("userId", userId);
  }
}
