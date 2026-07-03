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
public class UserResourceTest {

  @Test
  public void fetchSelfReturnsUserData() {
    Registered user = registerUser("fetchself");

    given()
        .header("Authorization", "Bearer " + user.token())
        .when()
        .get("/api/user/" + user.id())
        .then()
        .statusCode(200)
        .body("login", equalTo(user.login()));
  }

  @Test
  public void fetchOtherUserWithoutAdminIsForbidden() {
    Registered user = registerUser("owner");
    Registered other = registerUser("intruder");

    given()
        .header("Authorization", "Bearer " + other.token())
        .when()
        .get("/api/user/" + user.id())
        .then()
        .statusCode(403);
  }

  @Test
  public void updateDisplayNameIsPersisted() {
    Registered user = registerUser("rename");

    given()
        .header("Authorization", "Bearer " + user.token())
        .contentType(ContentType.JSON)
        .body(Map.of("displayName", "Nouveau Nom"))
        .when()
        .put("/api/user/" + user.id())
        .then()
        .statusCode(200)
        .body("displayName", equalTo("Nouveau Nom"));

    given()
        .header("Authorization", "Bearer " + user.token())
        .when()
        .get("/api/user/" + user.id())
        .then()
        .statusCode(200)
        .body("displayName", equalTo("Nouveau Nom"));
  }

  @Test
  public void updateWithoutAuthenticationIsUnauthorized() {
    Registered user = registerUser("noauth");

    given()
        .contentType(ContentType.JSON)
        .body(Map.of("displayName", "Hacked"))
        .when()
        .put("/api/user/" + user.id())
        .then()
        .statusCode(401);
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
