package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.converter.AchievementConverter;
import fr.epita.assistants.ping.data.model.AchievementModel;
import fr.epita.assistants.ping.data.model.UserAchievementModel;
import fr.epita.assistants.ping.data.repository.AchievementRepository;
import fr.epita.assistants.ping.data.repository.UserAchievementRepository;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/api/achievements")
@Produces(MediaType.APPLICATION_JSON)
public class AchievementResource {
  @Inject
  AchievementRepository achievementRepository;
  @Inject
  UserAchievementRepository userAchievementRepository;
  @Inject
  AchievementConverter achievementConverter;
  @Inject
  JsonWebToken jwt;

  @GET
  @Authenticated
  public Response fetchAchievements() {
    UUID userId = UUID.fromString(jwt.getSubject());
    List<Object> responses = new ArrayList<>();
    for (AchievementModel achievement : achievementRepository.listAll()) {
      UserAchievementModel unlocked = userAchievementRepository.findByUserAndAchievement(userId, achievement.getId());
      responses.add(achievementConverter.toResponse(achievement, unlocked));
    }
    return Response.ok(responses).build();
  }
}
