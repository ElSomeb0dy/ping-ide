package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.converter.FolderConverter;
import fr.epita.assistants.ping.data.model.FolderModel;
import fr.epita.assistants.ping.domain.service.FolderService;
import fr.epita.assistants.ping.presentation.api.request.PathRequest;
import fr.epita.assistants.ping.presentation.api.request.MoveRequest;
import fr.epita.assistants.ping.presentation.api.response.FSEntryResponse;
import fr.epita.assistants.ping.utils.HttpError;
import fr.epita.assistants.ping.utils.Logger;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@ApplicationScoped
@Path("/api/folders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FolderResource {

  @Inject
  JsonWebToken jwt;
  @Inject
  FolderService folderService;
  @Inject
  FolderConverter folderConverter;

  @GET
  @Authenticated
  public Response getFolders(@QueryParam("path") String path) {
    List<FolderModel> folderModels;

    try{
     folderModels  = folderService.fetchFolders(path);
    }
    catch(Exception e){
      Logger.error("GET /api/folders failed: id=" + jwt.getSubject() + ", path=" + path + ", reason=" + e.getMessage());
      throw e;
    }

    List<FSEntryResponse> responses = folderConverter.toResponseList(folderModels);

    Logger.log("GET /api/folders successful: id=" + jwt.getSubject() + ", path=" + path);
    return Response.ok(responses).build();
  }

  @POST
  @Authenticated
  public Response createFolder(PathRequest request) {
    if (request == null) {
      Logger.error("POST /api/folders failed: the request is null, id=" + jwt.getSubject());
      throw new HttpError(Response.Status.BAD_REQUEST,
          "The request cannot be empty").get();
    }

    try{
      folderService.createFolder(request.relativePath);
    }
    catch(Exception e){
      Logger.error("POST /api/folders failed: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath + ", reason" + e.getMessage());
      throw e;
    }

    Logger.log("POST /api/folders successful: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath);
    return Response.status(Response.Status.CREATED).build();
  }

  @DELETE
  @Authenticated
  public Response deleteFolder(PathRequest request) {
    if (request == null) {
      Logger.error("DELETE /api/folders failed: the request is null, id=" + jwt.getSubject());
      throw new HttpError(Response.Status.BAD_REQUEST,
          "The request cannot be empty").get();
    }

    try{
      folderService.deleteFolder(request.relativePath);
    }
    catch(Exception e){
      Logger.error("DELETE /api/folders failed: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("DELETE /api/folders successful: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath);
    return Response.noContent().build();
  }

  @PUT
  @Path("/move")
  @Authenticated
  public Response moveFolder(MoveRequest request) {

    if (request == null) {
      Logger.error("PUT /api/folders/move failed: the request is null, id=" + jwt.getSubject());
      throw new HttpError(Response.Status.BAD_REQUEST, "The request cannot be empty").get();
    }

    try {
      folderService.moveFolder(request.src, request.dst);
    }
    catch (Exception e) {
      Logger.error("PUT /api/folders/move failed: id=" + jwt.getSubject() + ", src=" + request.src + ", dst=" + request.dst + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("PUT /api/folders/move successful: id=" + jwt.getSubject() + ", src=" + request.src + ", dst=" + request.dst);

    return Response.noContent().build();
  }
}
