package fr.epita.assistants.ping;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestProfile(UserRegistrationTestProfile.class)
public class QuestResourceTest {

  @Test
  public void fetchDailyQuestsWithoutAuthIsUnauthorized() {
    given()
        .when()
        .get("/api/quests/daily")
        .then()
        .statusCode(401);
  }

  @Test
  public void fetchDailyQuestsReturnsList() {
    String token = registerUser("dailyquests");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/quests/daily")
        .then()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(0));
  }

  @Test
  public void claimUnknownQuestReturnsNotFound() {
    String token = registerUser("claimquest");

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .post("/api/quests/" + UUID.randomUUID() + "/claim")
        .then()
        .statusCode(404);
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
