package fr.epita.assistants.ping;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class UserRegistrationTestProfile implements QuarkusTestProfile {
  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of(
        "quarkus.devservices.enabled", "true",
        "quarkus.datasource.devservices.enabled", "true",
        "quarkus.datasource.devservices.image-name", "postgres:16-alpine");
  }
}
