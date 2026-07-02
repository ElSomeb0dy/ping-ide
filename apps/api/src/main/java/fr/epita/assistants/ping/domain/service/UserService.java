package fr.epita.assistants.ping.domain.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.errors.ErrorsCode;
import fr.epita.assistants.ping.presentation.api.request.RegisterRequest;
import fr.epita.assistants.ping.utils.HashUtils;
import fr.epita.assistants.ping.utils.HttpError;

import io.smallrye.jwt.build.Jwt;
import fr.epita.assistants.ping.utils.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class UserService {

  @Inject
  UserRepository userRepository;
  @Inject
  UserSettingsService userSettingsService;
  @Inject
  GamificationService gamificationService;
  @Inject
  QuestService questService;

  public record RegisterResult(String token, UserModel user) {
  }

  public List<UserModel> fetchAllUsers() {
    return userRepository.fetchAll();
  }

  public UserModel findById(UUID id) {
    return userRepository.findById(id);
  }

  @Transactional
  public UserModel createUser(String login, String password, Boolean isAdmin) {

    if (!isLoginValid(login)) {
      throw new HttpError(Status.BAD_REQUEST, "The login is invalid").get();
    }

    if (doesUserExists(login)) {
      throw new HttpError(Status.CONFLICT, "The login is already in use").get();
    }

    UserModel userModel = new UserModel();

    userModel.setLogin(login);
    userModel.setPassword(encryptPassword(password));
    userModel.setDisplayName(computeDisplayName(login));

    if (isAdmin == null) {
      userModel.setIsAdmin(false);
    } else {
      userModel.setIsAdmin(isAdmin);
    }

    userModel.setAvatar("");
    userModel.setXp(0);
    userModel.setLevel(1);
    userModel.setCurrentStreak(0);
    userModel.setLongestStreak(0);

    userRepository.createUser(userModel);
    userSettingsService.ensureDefaults(userModel.getId());

    return userModel;
  }

  public UserModel validateCredentials(String login, String password) {

    if (login == null || password == null) {
      throw new HttpError(Status.BAD_REQUEST, "The login or password is null").get();
    }

    UserModel userModel = userRepository.findByLogin(login);

    if (userModel == null) {
      throw new HttpError(Status.UNAUTHORIZED, "This user does not exists").get();
    }

    String decryptedPassword;

    try {
      decryptedPassword = HashUtils.decrypt(userModel.getPassword());
    } catch (Exception e) {
      throw new HttpError(Status.INTERNAL_SERVER_ERROR, "Failed to verify credentials").get();
    }

    if (!decryptedPassword.equals(password)) {
      throw new HttpError(Status.UNAUTHORIZED, "The password is invalid").get();
    }

    gamificationService.touchActivity(userModel.getId());
    questService.recordProgress(userModel.getId(), "LOGIN", 1);

    return userModel;
  }

  @Transactional
  public RegisterResult register(RegisterRequest request) {
    if (request == null) {
      throw new HttpError(Status.BAD_REQUEST, "The request cannot be empty").get();
    }

    if (request.username == null || request.username.isBlank()
        || request.email == null || request.email.isBlank()
        || request.password == null || request.password.isBlank()) {
      throw new HttpError(Status.BAD_REQUEST, "The username, email and password are required").get();
    }

    if (!isLoginValid(request.username)) {
      throw new HttpError(Status.BAD_REQUEST, "The username is invalid").get();
    }

    if (doesUserExists(request.username) || userRepository.findByEmail(request.email) != null) {
      ErrorsCode.USER_ALREADY_EXISTS.throwException();
    }

    UserModel userModel = new UserModel();
    userModel.setLogin(request.username);
    userModel.setEmail(request.email);
    userModel.setPassword(encryptPassword(request.password));
    userModel.setDisplayName(computeDisplayName(request.username));
    userModel.setIsAdmin(false);
    userModel.setAvatar("");
    userModel.setXp(0);
    userModel.setLevel(1);
    userModel.setCurrentStreak(0);
    userModel.setLongestStreak(0);

    userRepository.createUser(userModel);
    userSettingsService.ensureDefaults(userModel.getId());
    gamificationService.touchActivity(userModel.getId());
    questService.recordProgress(userModel.getId(), "LOGIN", 1);

    return new RegisterResult(generateToken(userModel), userModel);
  }

  public String generateToken(UserModel userModel) {

    return Jwt.issuer("ping")
        .subject(userModel.getId().toString())
        .groups(Set.of(userModel.getIsAdmin() ? "admin" : "user"))
        .issuedAt(System.currentTimeMillis() / 1000)
        .expiresIn(3600)
        .sign();
  }

  private Boolean isLoginValid(String login) {
    if (login == null) {
      return false;
    }

    return login.matches("^[a-zA-Z0-9]+[._][a-zA-Z0-9]+$");
  }

  private Boolean doesUserExists(String login) {
    return userRepository.findByLogin(login) != null;
  }

  private String encryptPassword(String password) {

    String encryptedPassword;

    try {
      encryptedPassword = HashUtils.encrypt(password);
    } catch (Exception e) {
      throw new HttpError(Status.INTERNAL_SERVER_ERROR, "Failed to encrypt the password").get();
    }

    return encryptedPassword;
  }

  private String computeDisplayName(String login) {

    String separator = login.contains(".") ? "\\." : "_";
    String[] parts = login.split(separator);

    StringBuilder displayName = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];

      if (!part.isEmpty()) {
        displayName.append(Character.toUpperCase(part.charAt(0)))
            .append(part.substring(1));

        if (i < parts.length - 1) {
          displayName.append(" ");
        }
      }
    }

    return displayName.toString();
  }

  @Transactional
  public UserModel updateUser(UUID id, String password, String displayName, String avatar) {
    UserModel user = findById(id);

    if (user == null) {
      throw new HttpError(Status.NOT_FOUND, "The user does not exist").get();
    }

    if (password != null && !password.isBlank()) {
      user.setPassword(encryptPassword(password));
    }

    if (displayName != null && !displayName.isBlank()) {
      user.setDisplayName(displayName);
    }

    if (avatar != null) {
      user.setAvatar(avatar);
    }

    return user;
  }

  @Transactional
  public void deleteUser(UUID id) {
    Long deletedUser = userRepository.deleteById(id);

    if (deletedUser == 0) {
      throw new HttpError(Response.Status.NOT_FOUND, "The user does not exist").get();
    }
  }
}
