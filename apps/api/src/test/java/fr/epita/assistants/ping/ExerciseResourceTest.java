package fr.epita.assistants.ping;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestProfile(UserRegistrationTestProfile.class)
public class ExerciseResourceTest {

  @Test
  public void fetchExerciseWithoutAuthIsUnauthorized() {
    given()
        .when()
        .get("/api/exercises/" + UUID.randomUUID())
        .then()
        .statusCode(401);
  }

  @Test
  public void fetchUnknownExerciseReturnsNotFound() {
    String token = registerUser("exercisenotfound");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/exercises/" + UUID.randomUUID())
        .then()
        .statusCode(404);
  }

  @Test
  public void submitWithoutAuthIsUnauthorized() {
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("language", "Python", "code", "print('hi')"))
        .when()
        .post("/api/exercises/" + UUID.randomUUID() + "/submit")
        .then()
        .statusCode(401);
  }

  @Test
  public void submitWithMissingFieldsIsBadRequest() {
    String token = registerUser("submitmissing");

    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(Map.of())
        .when()
        .post("/api/exercises/" + UUID.randomUUID() + "/submit")
        .then()
        .statusCode(400)
        .body("message", equalTo("language and code are required"));
  }

  @Test
  public void submitUnknownExerciseReturnsNotFound() {
    String token = registerUser("submitnotfound");

    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(Map.of("language", "Python", "code", "print('hi')"))
        .when()
        .post("/api/exercises/" + UUID.randomUUID() + "/submit")
        .then()
        .statusCode(404)
        .body("message", equalTo("Exercise not found"));
  }

  private String registerUser(String prefix) {
    String suffix = Long.toString(System.nanoTime());
    String username = prefix + "." + suffix;

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
