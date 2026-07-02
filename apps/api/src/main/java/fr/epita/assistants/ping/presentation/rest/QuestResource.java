package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.domain.service.QuestService;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@ApplicationScoped
@Path("/api/quests")
@Produces(MediaType.APPLICATION_JSON)
public class QuestResource {
  @Inject
  JsonWebToken jwt;
  @Inject
  QuestService questService;

  @GET
  @Path("/daily")
  @Authenticated
  public Response fetchDailyQuests() {
    UUID userId = UUID.fromString(jwt.getSubject());
    return Response.ok(questService.fetchDailyQuests(userId)).build();
  }

  @POST
  @Path("/{id}/claim")
  @Authenticated
  public Response claimQuest(@PathParam("id") UUID id) {
    UUID userId = UUID.fromString(jwt.getSubject());
    return Response.ok(questService.claim(userId, id)).build();
  }
}
