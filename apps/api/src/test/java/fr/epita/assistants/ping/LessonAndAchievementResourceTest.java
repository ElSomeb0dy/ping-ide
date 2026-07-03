package fr.epita.assistants.ping;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@TestProfile(UserRegistrationTestProfile.class)
public class LessonAndAchievementResourceTest {

  @Test
  public void fetchLessonsWithoutAuthIsUnauthorized() {
    given()
        .when()
        .get("/api/lessons")
        .then()
        .statusCode(401);
  }

  @Test
  public void fetchLessonsReturnsSeededContent() {
    String token = registerUser("lessons");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/lessons")
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
  }

  @Test
  public void fetchUnknownLessonReturnsNotFound() {
    String token = registerUser("lessonnotfound");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/lessons/" + UUID.randomUUID())
        .then()
        .statusCode(404);
  }

  @Test
  public void fetchAchievementsReturnsSeededContent() {
    String token = registerUser("achievements");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/achievements")
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
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
