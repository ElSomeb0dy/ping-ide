package fr.epita.assistants.ping.presentation.rest;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.epita.assistants.ping.domain.service.FileService;
import fr.epita.assistants.ping.utils.Logger;
import fr.epita.assistants.ping.utils.HttpError;
import fr.epita.assistants.ping.presentation.api.request.MoveRequest;
import fr.epita.assistants.ping.presentation.api.request.PathRequest;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@ApplicationScoped
@Path("/api")
public class FileResource {

  @Inject
  FileService fileService;

  @Inject
  JsonWebToken jwt;

  @GET
  @Path("/files")
  @Authenticated
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFile(@QueryParam("path") String path) {

    byte[] content;

    try{
      content = fileService.getFileContent(path);
    }
    catch(Exception e){
      Logger.error("GET /api/files failed: id=" + jwt.getSubject() + ", path=" + path + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("GET /api/files successful: id=" + jwt.getSubject() + ", path=" + path);

    return Response.ok(content).build();
  }

  @POST
  @Path("/files")
  @Authenticated
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createFile(PathRequest request) {

    if (request == null) {
      Logger.error("POST /files failed: the request is null with id=" + jwt.getSubject());
      throw new HttpError(Status.BAD_REQUEST, "The request can't be empty").get();
    }

    try{
      fileService.createFile(request.relativePath);
    }
    catch(Exception e){
      Logger.error("POST /api/files failed: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("POST /api/files successful: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath);

    return Response.status(Status.CREATED).build();
  }

  @DELETE
  @Path("/files")
  @Authenticated
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteFile(PathRequest request) {

    if (request == null) {
      Logger.error("DELETE /api/files failed: the request is null, id=" + jwt.getSubject());
      throw new HttpError(Status.BAD_REQUEST, "The request cannot be empty").get();
    }
    try{
      fileService.deleteFile(request.relativePath);
    }
    catch(Exception e){
      Logger.error("DELETE /api/files failed: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("DELETE /api/files successful: id=" + jwt.getSubject() + ", relativePath=" + request.relativePath);

    return Response.noContent().build();
  }

  @PUT
  @Path("/files/move")
  @Authenticated
  @Consumes(MediaType.APPLICATION_JSON)
  public Response moveFile(MoveRequest request) {

    if (request == null) {
      Logger.error("PUT /api/files/move failed: the request is null, id=" + jwt.getSubject());
      throw new HttpError(Status.BAD_REQUEST, "The request cannot be empty").get();
    }
    try{
      fileService.moveFile(request.src, request.dst);
    }
    catch(Exception e){
      Logger.error("PUT /api/files/move failed: id=" + jwt.getSubject() + ", src=" + request.src + ", dst=" + request.dst + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("PUT /api/files/move successful: id=" + jwt.getSubject() + ", src=" + request.src + ", dst=" + request.dst);

    return Response.noContent().build();
  }

  @POST
  @Path("/files/upload")
  @Authenticated
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  public Response uploadFile(@QueryParam("path") String path, byte[] content) {

    if (path == null || path.isBlank()) {
      Logger.error("POST /api/files/upload failed: the path is null, id=" + jwt.getSubject());
      throw new HttpError(Status.BAD_REQUEST, "The path cannot be empty").get();
    }

    try{
      fileService.uploadFile(path, content);
    }
    catch (Exception e){
      Logger.error("POST /api/files/upload failed: id=" + jwt.getSubject() + ", path=" + path + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("POST /api/files/upload successful: id=" + jwt.getSubject() + ", path=" + path);
    return Response.status(Status.CREATED).build();
  }
}
