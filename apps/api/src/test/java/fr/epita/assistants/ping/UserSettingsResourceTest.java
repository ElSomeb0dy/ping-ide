package fr.epita.assistants.ping;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestProfile(UserRegistrationTestProfile.class)
public class UserSettingsResourceTest {

  @Test
  public void fetchSettingsReturnsDefaults() {
    Registered user = registerUser("settingsdefault");

    given()
        .header("Authorization", "Bearer " + user.token())
        .when()
        .get("/api/user/" + user.id() + "/settings")
        .then()
        .statusCode(200)
        .body("notificationsEnabled", equalTo(true));
  }

  @Test
  public void updateSettingsPersistsToggle() {
    Registered user = registerUser("settingsupdate");

    given()
        .header("Authorization", "Bearer " + user.token())
        .contentType(ContentType.JSON)
        .body(Map.of("notificationsEnabled", false))
        .when()
        .put("/api/user/" + user.id() + "/settings")
        .then()
        .statusCode(200)
        .body("notificationsEnabled", equalTo(false));

    given()
        .header("Authorization", "Bearer " + user.token())
        .when()
        .get("/api/user/" + user.id() + "/settings")
        .then()
        .statusCode(200)
        .body("notificationsEnabled", equalTo(false));
  }

  private Registered registerUser(String prefix) {
    String suffix = Long.toString(System.nanoTime());
    String username = prefix + "." + suffix;

    io.restassured.response.Response response = given()
        .contentType(ContentType.JSON)
        .body(Map.of(
            "username", username,
            "email", username + "@ping.local",
            "password", "password",
            "confirmPassword", "password"))
        .when()
        .post("/api/user/register")
        .then()
        .statusCode(200)
        .extract()
        .response();

    return new Registered(response.path("token"), response.path("user.id"), username);
  }

  private record Registered(String token, String id, String login) {
  }
}
