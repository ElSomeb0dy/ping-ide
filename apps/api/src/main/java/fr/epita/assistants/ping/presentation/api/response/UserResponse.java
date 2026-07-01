package fr.epita.assistants.ping.presentation.api.response;

import java.util.UUID;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserResponse {
  public UUID id;

  public String login;
  public String displayName;

  public Boolean isAdmin;

  public String avatar;
}
