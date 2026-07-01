package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.converter.QuestConverter;
import fr.epita.assistants.ping.data.model.QuestModel;
import fr.epita.assistants.ping.data.model.UserQuestModel;
import fr.epita.assistants.ping.data.repository.QuestRepository;
import fr.epita.assistants.ping.data.repository.UserQuestRepository;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/api/quests")
@Produces(MediaType.APPLICATION_JSON)
public class QuestResource {
  @Inject
  JsonWebToken jwt;
  @Inject
  QuestRepository questRepository;
  @Inject
  UserQuestRepository userQuestRepository;
  @Inject
  QuestConverter questConverter;

  @GET
  @Path("/daily")
  @Authenticated
  @Transactional
  public Response fetchDailyQuests() {
    UUID userId = UUID.fromString(jwt.getSubject());
    LocalDate today = LocalDate.now();
    List<QuestModel> quests = questRepository.listAll();
    List<Object> responses = new ArrayList<>();

    for (QuestModel quest : quests) {
      UserQuestModel userQuest = userQuestRepository.findByUserQuestAndDate(userId, quest.getId(), today);
      if (userQuest == null) {
        userQuest = new UserQuestModel();
        userQuest.setUserId(userId);
        userQuest.setQuestId(quest.getId());
        userQuest.setDate(today);
        userQuest.setProgressTarget(quest.getCriteria());
        userQuestRepository.persist(userQuest);
      }
      responses.add(questConverter.toResponse(quest, userQuest));
    }

    return Response.ok(responses).build();
  }
}
