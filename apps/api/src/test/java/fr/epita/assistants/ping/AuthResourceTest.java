package fr.epita.assistants.ping;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

@QuarkusTest
@TestProfile(UserRegistrationTestProfile.class)
public class AuthResourceTest {

  @Test
  public void loginWithValidCredentialsReturnsToken() {
    String suffix = Long.toString(System.nanoTime());
    String username = "login." + suffix;
    registerUser(username);

    given()
        .contentType(ContentType.JSON)
        .body(Map.of("login", username, "password", "password"))
        .when()
        .post("/api/user/login")
        .then()
        .statusCode(200)
        .body("token", not(emptyString()))
        .body("user.login", equalTo(username));
  }

  @Test
  public void loginWithWrongPasswordIsUnauthorized() {
    String suffix = Long.toString(System.nanoTime());
    String username = "badpass." + suffix;
    registerUser(username);

    given()
        .contentType(ContentType.JSON)
        .body(Map.of("login", username, "password", "wrong-password"))
        .when()
        .post("/api/user/login")
        .then()
        .statusCode(401);
  }

  @Test
  public void loginWithUnknownUserIsUnauthorized() {
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("login", "does-not-exist", "password", "password"))
        .when()
        .post("/api/user/login")
        .then()
        .statusCode(401);
  }

  @Test
  public void refreshWithoutTokenIsUnauthorized() {
    given()
        .when()
        .get("/api/user/refresh")
        .then()
        .statusCode(401);
  }

  @Test
  public void refreshWithValidTokenReturnsNewToken() {
    String suffix = Long.toString(System.nanoTime());
    String username = "refresh." + suffix;
    String token = registerUser(username);

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/user/refresh")
        .then()
        .statusCode(200)
        .body("token", not(emptyString()))
        .body("user.login", equalTo(username));
  }

  private String registerUser(String username) {
    return given()
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
        .path("token");
  }
}
