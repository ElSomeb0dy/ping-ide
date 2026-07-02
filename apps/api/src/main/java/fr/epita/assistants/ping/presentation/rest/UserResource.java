package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.presentation.api.request.UpdateUserRequest;
import fr.epita.assistants.ping.presentation.api.request.UserSettingsRequest;
import fr.epita.assistants.ping.presentation.api.response.LoginResponse;
import fr.epita.assistants.ping.presentation.api.response.UserSettingsResponse;
import fr.epita.assistants.ping.presentation.api.response.UserStatsResponse;
import fr.epita.assistants.ping.utils.HttpError;
import io.quarkus.security.Authenticated;
import fr.epita.assistants.ping.converter.UserConverter;
import fr.epita.assistants.ping.data.model.UserSettingsModel;
import fr.epita.assistants.ping.data.repository.UserAchievementRepository;
import fr.epita.assistants.ping.data.repository.UserExerciseProgressRepository;
import fr.epita.assistants.ping.data.repository.UserLessonProgressRepository;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.domain.service.GamificationService;
import fr.epita.assistants.ping.domain.service.UserSettingsService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.presentation.api.request.LoginRequest;
import fr.epita.assistants.ping.presentation.api.request.NewUserRequest;
import fr.epita.assistants.ping.presentation.api.request.RegisterRequest;
import fr.epita.assistants.ping.presentation.api.response.UserResponse;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.epita.assistants.ping.utils.Logger;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

  @Inject
  UserService userService;
  @Inject
  UserConverter userConverter;
  @Inject
  UserExerciseProgressRepository userExerciseProgressRepository;
  @Inject
  UserLessonProgressRepository userLessonProgressRepository;
  @Inject
  UserAchievementRepository userAchievementRepository;
  @Inject
  UserSettingsService userSettingsService;
  @Inject
  GamificationService gamificationService;

  @Inject
  JsonWebToken jwt;

  @POST
  @Path("/user")
  @RolesAllowed("admin")
  public Response createUser(NewUserRequest request) {

    if (request == null) {
      Logger.error("POST /api/user failed: the request is null, id=" + jwt.getSubject());
      throw new HttpError(Response.Status.BAD_REQUEST, "The request cannot be empty").get();
    }

    if (request.login == null || request.password == null) {
      Logger.error("POST /api/user failed: login=" + request.login + " or password is null, id=" + jwt.getSubject());
      throw new HttpError(Response.Status.BAD_REQUEST, "The login or the password is null").get();
    }

    UserModel userModel;
    try{
      userModel = userService.createUser(
              request.login,
              request.password,
              request.isAdmin);
    }
    catch(Exception e){
      Logger.error("POST /api/user failed: id=" + jwt.getSubject() + ", login=" + request.login + ", isAdmin=" + request.isAdmin + ", reason=" + e.getMessage());
      throw e;
    }

    UserResponse response = userConverter.toUserResponse(userModel);

    Logger.log("POST /api/user successful: id=" + jwt.getSubject() + ", login=" + request.login + ", isAdmin=" + request.isAdmin);

    return Response.ok(response).build();
  }

  @GET
  @Path("/user/all")
  @RolesAllowed("admin")
  public Response fetchAllUsers() {

    List<UserModel> userModels = userService.fetchAllUsers();
    List<UserResponse> response = userConverter.toResponseList(userModels);

    Logger.log("GET /api/user/all successful, id=" + jwt.getSubject());

    return Response.ok(response).build();
  }

  @POST
  @Path("/user/login")
  public Response login(LoginRequest request) {
    if (request == null) {
      Logger.error("POST /api/user/login failed: the request is null");
      throw new HttpError(Response.Status.BAD_REQUEST, "The request cannot be empty").get();
    }
    UserModel authenticatedUser;
    try{
      authenticatedUser  = userService.validateCredentials(request.login, request.password);
    }
    catch(Exception e){
      Logger.error("POST /api/user/login failed: login=" + request.login + ", reason=" + e.getMessage());
      throw e;
    }

    String token = userService.generateToken(authenticatedUser);

    Logger.log(
        "POST /api/user/login successful with id=" + authenticatedUser.getId() + ", login=" + request.login + ", authenticatedUserID=" + jwt.getSubject());

    return Response.ok(new LoginResponse(token, userConverter.toUserResponse(authenticatedUser))).build();
  }

  @POST
  @Path("/user/register")
  public Response register(RegisterRequest request) {
    UserService.RegisterResult result;
    try {
      result = userService.register(request);
    } catch (Exception e) {
      Logger.error("POST /api/user/register failed: username=" + (request == null ? null : request.username)
          + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("POST /api/user/register successful: id=" + result.user().getId()
        + ", login=" + result.user().getLogin());

    return Response.ok(new LoginResponse(result.token(), userConverter.toUserResponse(result.user()))).build();
  }

  @GET
  @Path("/user/refresh")
  @Authenticated
  public Response refresh() {
    UUID userId = UUID.fromString(jwt.getSubject());

    UserModel userModel = userService.findById(userId);

    if (userModel == null) {
      Logger.error("GET /api/user/refresh failed: the user does not exists, id=" + jwt.getSubject());
      throw new HttpError(Response.Status.NOT_FOUND, "User not found").get();
    }

    String token = userService.generateToken(userModel);

    Logger.log("GET /api/user/login successful with authenticatedUserID=" + jwt.getSubject() + ", login=" + userModel.getLogin()
        + ", isAdmin=" + jwt.getGroups());

    return Response.ok(new LoginResponse(token, userConverter.toUserResponse(userModel))).build();
  }

  @GET
  @Path("/user/{id}")
  @Authenticated
  public Response fetchUser(@PathParam("id") UUID id) {
    String authenticatedUser = jwt.getSubject();

    Boolean isSelf = authenticatedUser.equals(id.toString());
    Boolean isAdmin = jwt.getGroups().contains("admin");

    if (!isSelf && !isAdmin) {
      Logger.error("GET /api/user/{id} failed: id=" + id + ", authenticatedUserId=" + jwt.getSubject() + " is not authorized");
      throw new HttpError(Response.Status.FORBIDDEN, "You are not authorized to access this data").get();
    }

    UserModel user = userService.findById(id);
    if (user == null) {
      Logger.error("GET /api/user/{id} failed: the user with id=" + id + " does not exist, authenticatedUserId=" + jwt.getSubject());
      throw new HttpError(Response.Status.NOT_FOUND, "User not found").get();
    }

    Logger.log("GET /api/user/{id} successful: id=" + id + ", authenticatedUserId=" + jwt.getSubject());

    return Response.ok(userConverter.toUserResponse(user)).build();
  }

  @GET
  @Path("/user/{id}/stats")
  @Authenticated
  public Response fetchUserStats(@PathParam("id") UUID id) {
    assertSelfOrAdmin(id, "GET /api/user/{id}/stats");

    UserModel user = userService.findById(id);
    if (user == null) {
      throw new HttpError(Response.Status.NOT_FOUND, "User not found").get();
    }

    Long lessonsCompleted = userLessonProgressRepository.count("userId = ?1 AND status = ?2", id, "COMPLETED");
    Long exercisesSolved = userExerciseProgressRepository.count("userId = ?1 AND status = ?2", id, "COMPLETED");
    Long achievementsUnlocked = userAchievementRepository.count("userId", id);

    UserStatsResponse response = new UserStatsResponse(
        user.getLevel(),
        user.getXp(),
        gamificationService.xpForNextLevel(user.getLevel()),
        user.getCurrentStreak(),
        user.getLongestStreak(),
        lessonsCompleted,
        exercisesSolved,
        achievementsUnlocked);

    return Response.ok(response).build();
  }

  @GET
  @Path("/user/{id}/settings")
  @Authenticated
  public Response fetchUserSettings(@PathParam("id") UUID id) {
    assertSelfOrAdmin(id, "GET /api/user/{id}/settings");

    UserSettingsModel settings = userSettingsService.ensureDefaults(id);
    return Response.ok(toSettingsResponse(settings)).build();
  }

  @PUT
  @Path("/user/{id}/settings")
  @Authenticated
  public Response updateUserSettings(@PathParam("id") UUID id, UserSettingsRequest request) {
    assertSelfOrAdmin(id, "PUT /api/user/{id}/settings");

    UserSettingsModel settings = userSettingsService.update(id, request);
    return Response.ok(toSettingsResponse(settings)).build();
  }

  @DELETE
  @Path("/user/{id}")
  @RolesAllowed("admin")
  public Response deleteUser(@PathParam("id") UUID id) {

    try{
      userService.deleteUser(id);
    }
    catch(Exception e){
      Logger.error("DELETE /api/user/{id} failed: id=" + id + ", authenticatedUserId=" + jwt.getSubject() + ", reason=" + e.getMessage());
      throw e;
    }

    Logger.log("DELETE /api/user/{id} successful: id=" + id + ", authenticatedUserId=" + jwt.getSubject());

    return Response.noContent().build();

  }

  @PUT
  @Path("/user/{id}")
  @Authenticated
  public Response updateUser(@PathParam("id") UUID id, UpdateUserRequest request) {
    if (request == null) {
      Logger.error("PUT /api/user/{id} failed: the request is null, id=" + id + ", authenticatedUserId=" + jwt.getSubject());
      throw new HttpError(Response.Status.BAD_REQUEST, "The request cannot be empty").get();
    }

    Boolean isAdmin = jwt.getGroups().contains("admin");
    Boolean isSameUser = jwt.getSubject().equals(id.toString());

    if (!isAdmin && !isSameUser) {
      Logger.error("PUT /api/user/{id} failed: id=" + id + ", authenticatedUserId=" + jwt.getSubject() + " is not allowed to update this user");
      throw new HttpError(Response.Status.FORBIDDEN, "You are not allowed to update this user").get();
    }

    UserModel userModel = userService.findById(id);
    if (userModel == null) {
      Logger.error("PUT /api/user/{id} failed: the user with id=" + id + " does not exist, authenticatedUserId=" + jwt.getSubject());
      throw new HttpError(Response.Status.NOT_FOUND, "User not found").get();
    }

    UserModel updatedUser;
    try{
     updatedUser = userService.updateUser(id, request.password, request.displayName, request.avatar);
    }
    catch(Exception e){
      Logger.error("PUT /api/user/{id} failed: id=" + id + ", displayName=" + request.displayName + ", authenticatedUserId=" + jwt.getSubject() + ", reason=" + e.getMessage());
      throw e;
    }

    UserResponse response = userConverter.toUserResponse(updatedUser);

    Logger.log("PUT /api/user/{id} successful: id=" + id + ", displayName=" + request.displayName + ", authenticatedUserId=" + jwt.getSubject());

    return Response.ok(response).build();
  }

  private void assertSelfOrAdmin(UUID id, String route) {
    Boolean isAdmin = jwt.getGroups().contains("admin");
    Boolean isSameUser = jwt.getSubject().equals(id.toString());

    if (!isAdmin && !isSameUser) {
      Logger.error(route + " failed: id=" + id + ", authenticatedUserId=" + jwt.getSubject() + " is not authorized");
      throw new HttpError(Response.Status.FORBIDDEN, "You are not authorized to access this data").get();
    }
  }

  private UserSettingsResponse toSettingsResponse(UserSettingsModel settings) {
    return new UserSettingsResponse(
        settings.getTheme(),
        settings.getDefaultLanguage(),
        settings.getNotificationsEnabled(),
        settings.getSoundEnabled());
  }
}
