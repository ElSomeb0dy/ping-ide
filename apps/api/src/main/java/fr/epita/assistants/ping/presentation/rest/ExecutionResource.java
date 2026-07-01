package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.domain.service.CodeExecutionService;
import fr.epita.assistants.ping.presentation.api.request.ExecuteRequest;
import fr.epita.assistants.ping.presentation.api.response.ExecuteResponse;
import fr.epita.assistants.ping.utils.HttpError;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/api/execute")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExecutionResource {
  @Inject
  CodeExecutionService codeExecutionService;

  @POST
  @Authenticated
  public Response execute(ExecuteRequest request) {
    if (request == null || request.language == null || request.code == null) {
      throw new HttpError(Response.Status.BAD_REQUEST, "language and code are required").get();
    }

    CodeExecutionService.ExecutionResult result = codeExecutionService.run(request.language, request.code, request.stdin);
    return Response.ok(new ExecuteResponse(result.stdout(), result.stderr(), result.exitCode(), result.timedOut())).build();
  }
}
