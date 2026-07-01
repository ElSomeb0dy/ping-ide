package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.model.UserSettingsModel;
import fr.epita.assistants.ping.data.repository.UserSettingsRepository;
import fr.epita.assistants.ping.presentation.api.request.UserSettingsRequest;
import fr.epita.assistants.ping.utils.HttpError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@ApplicationScoped
public class UserSettingsService {
  @Inject
  UserSettingsRepository userSettingsRepository;

  @Transactional
  public UserSettingsModel ensureDefaults(UUID userId) {
    UserSettingsModel existing = userSettingsRepository.findByUserId(userId);
    if (existing != null) {
      return existing;
    }

    UserSettingsModel settings = new UserSettingsModel();
    settings.setUserId(userId);
    userSettingsRepository.persist(settings);
    return settings;
  }

  public UserSettingsModel findByUserId(UUID userId) {
    UserSettingsModel settings = userSettingsRepository.findByUserId(userId);
    if (settings == null) {
      throw new HttpError(Response.Status.NOT_FOUND, "Settings not found").get();
    }
    return settings;
  }

  @Transactional
  public UserSettingsModel update(UUID userId, UserSettingsRequest request) {
    if (request == null) {
      throw new HttpError(Response.Status.BAD_REQUEST, "The request cannot be empty").get();
    }

    UserSettingsModel settings = ensureDefaults(userId);
    if (request.theme != null && !request.theme.isBlank()) {
      settings.setTheme(request.theme);
    }
    if (request.defaultLanguage != null && !request.defaultLanguage.isBlank()) {
      settings.setDefaultLanguage(request.defaultLanguage);
    }
    if (request.notificationsEnabled != null) {
      settings.setNotificationsEnabled(request.notificationsEnabled);
    }
    if (request.soundEnabled != null) {
      settings.setSoundEnabled(request.soundEnabled);
    }

    return settings;
  }
}
