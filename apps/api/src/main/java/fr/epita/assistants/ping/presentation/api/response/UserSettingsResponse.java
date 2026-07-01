package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserSettingsResponse {
  public String theme;
  public String defaultLanguage;
  public Boolean notificationsEnabled;
  public Boolean soundEnabled;
}
