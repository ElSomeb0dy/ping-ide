package fr.epita.assistants.ping;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;

@QuarkusTest
@TestProfile(UserRegistrationTestProfile.class)
public class UserRegistrationResourceTest {

  @Test
  public void successfulRegistrationReturnsTokenAndUser() {
    String suffix = Long.toString(System.nanoTime());
    String username = "register." + suffix;
    String email = username + "@ping.local";

    given()
        .contentType(ContentType.JSON)
        .body(registerBody(username, email))
        .when()
        .post("/api/user/register")
        .then()
        .statusCode(200)
        .body("token", not(emptyString()))
        .body("user.login", equalTo(username))
        .body("user.isAdmin", equalTo(false));
  }

  @Test
  public void duplicateUsernameOrEmailReturnsConflict() {
    String suffix = Long.toString(System.nanoTime());
    String username = "duplicate." + suffix;
    String email = username + "@ping.local";

    given()
        .contentType(ContentType.JSON)
        .body(registerBody(username, email))
        .when()
        .post("/api/user/register")
        .then()
        .statusCode(200);

    given()
        .contentType(ContentType.JSON)
        .body(registerBody(username, "other." + suffix + "@ping.local"))
        .when()
        .post("/api/user/register")
        .then()
        .statusCode(409)
        .body("message", equalTo("Username or email is already in use"));

    given()
        .contentType(ContentType.JSON)
        .body(registerBody("other." + suffix, email))
        .when()
        .post("/api/user/register")
        .then()
        .statusCode(409)
        .body("message", equalTo("Username or email is already in use"));
  }

  private Map<String, String> registerBody(String username, String email) {
    return Map.of(
        "username", username,
        "email", email,
        "password", "password",
        "confirmPassword", "password");
  }
}
