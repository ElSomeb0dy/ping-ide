package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.data.model.AchievementModel;
import fr.epita.assistants.ping.data.model.ExerciseModel;
import fr.epita.assistants.ping.data.model.LessonModel;
import fr.epita.assistants.ping.data.model.QuestModel;
import fr.epita.assistants.ping.data.model.TestCaseModel;
import fr.epita.assistants.ping.data.repository.AchievementRepository;
import fr.epita.assistants.ping.data.repository.ExerciseRepository;
import fr.epita.assistants.ping.data.repository.LessonRepository;
import fr.epita.assistants.ping.data.repository.QuestRepository;
import fr.epita.assistants.ping.data.repository.TestCaseRepository;
import fr.epita.assistants.ping.utils.Logger;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DevBootstrapService {
  private static final String ALLOWED_LANGUAGES = "[\"Python\"]";

  @Inject
  UserRepository userRepository;

  @Inject
  UserService userService;
  @Inject
  LessonRepository lessonRepository;
  @Inject
  ExerciseRepository exerciseRepository;
  @Inject
  AchievementRepository achievementRepository;
  @Inject
  QuestRepository questRepository;
  @Inject
  TestCaseRepository testCaseRepository;

  @ConfigProperty(name = "ping.bootstrap.admin.enabled", defaultValue = "true")
  Boolean bootstrapAdminEnabled;

  @ConfigProperty(name = "ping.bootstrap.admin.login", defaultValue = "admin.admin")
  String bootstrapAdminLogin;

  @ConfigProperty(name = "ping.bootstrap.admin.password", defaultValue = "admin")
  String bootstrapAdminPassword;

  @Transactional
  void onStart(@Observes StartupEvent event) {
    seedContent();

    if (!Boolean.TRUE.equals(bootstrapAdminEnabled)) {
      return;
    }

    if (userRepository.findByLogin(bootstrapAdminLogin) != null) {
      Logger.log("Bootstrap admin already exists: login=" + bootstrapAdminLogin);
      return;
    }

    userService.createUser(bootstrapAdminLogin, bootstrapAdminPassword, true);
    Logger.log("Bootstrap admin created: login=" + bootstrapAdminLogin);
  }

  private void seedContent() {
    LessonModel arrays = ensureLesson(
        "arrays-basics",
        "Les bases des tableaux",
        "Découvre comment manipuler des tableaux et parcourir leurs éléments efficacement.",
        1,
        "Facile",
        "Tableaux",
        null);
    ensureExercise(
        arrays.getId(),
        "two-sum",
        "Two Sum",
        "Retourne les indices de deux nombres dont la somme vaut la cible. Pour tester le backend, la solution est déjà écrite.",
        1,
        "Facile",
        30,
        "nums = [2, 7, 11, 15], target = 9",
        "[0, 1]",
        "print(\"[0, 1]\")\n");
    ensureExercise(
        arrays.getId(),
        "max-value",
        "Maximum d'un tableau",
        "Parcours un tableau pour trouver sa plus grande valeur. La réponse est préremplie pour valider le flux.",
        2,
        "Facile",
        25,
        "nums = [4, 2, 15, 8]",
        "15",
        "print(15)\n");
    ensureExercise(
        arrays.getId(),
        "reverse-array",
        "Inverser un tableau",
        "Construis une version inversée d'un tableau. La solution de test est déjà dans l'éditeur.",
        3,
        "Facile",
        25,
        "nums = [1, 2, 3]",
        "[3, 2, 1]",
        "print(\"[3, 2, 1]\")\n");

    LessonModel loops = ensureLesson(
        "loops-conditions",
        "Boucles et conditions",
        "Maîtrise les structures de contrôle pour résoudre des problèmes algorithmiques.",
        2,
        "Facile",
        "Algorithmie",
        arrays.getId());
    ensureExercise(
        loops.getId(),
        "fizzbuzz",
        "FizzBuzz",
        "Utilise conditions et modulo pour produire la bonne séquence. La sortie attendue est déjà produite.",
        1,
        "Facile",
        30,
        "n = 5",
        "1\n2\nFizz\n4\nBuzz",
        "print(\"1\")\nprint(\"2\")\nprint(\"Fizz\")\nprint(\"4\")\nprint(\"Buzz\")\n");
    ensureExercise(
        loops.getId(),
        "count-positives",
        "Compter les positifs",
        "Compte les valeurs strictement positives dans une liste. La réponse est préremplie.",
        2,
        "Facile",
        25,
        "nums = [-1, 3, 0, 7, 2]",
        "3",
        "print(3)\n");

    LessonModel recursion = ensureLesson(
        "recursion",
        "Récursivité",
        "Apprends à décomposer un problème en sous-problèmes plus simples.",
        3,
        "Moyen",
        "Algorithmie",
        loops.getId());
    ensureExercise(
        recursion.getId(),
        "factorial",
        "Factorielle",
        "Implémente une fonction factorielle récursive. La solution est déjà présente pour tester la soumission.",
        1,
        "Moyen",
        40,
        "n = 5",
        "120",
        "def factorial(n):\n    if n <= 1:\n        return 1\n    return n * factorial(n - 1)\n\nprint(factorial(5))\n");

    ensureAchievement("FIRST_EXERCISE", "Premier pas", "Termine ton tout premier exercice.", "Star", "EXERCISES_COMPLETED", 1, 30);
    ensureAchievement("FIVE_EXERCISES", "Marathonien junior", "Termine 5 exercices au total.", "Trophy", "EXERCISES_COMPLETED", 5, 50);
    ensureAchievement("FIRST_LESSON", "Leçon maîtrisée", "Termine une leçon complète.", "GraduationCap", "LESSONS_COMPLETED", 1, 50);
    ensureAchievement("LEVEL_5", "Niveau 5", "Atteins le niveau 5.", "Zap", "LEVEL", 5, 75);

    ensureQuest("DAILY_LOGIN", "Connexion du jour", "Connecte-toi aujourd'hui.", 10, "LOGIN", 1);
    ensureQuest("SOLVE_ONE", "Résous 1 exercice", "Termine un exercice aujourd'hui.", 30, "EXERCISES_COMPLETED", 1);
    ensureQuest("EARN_XP", "Gagne 50 XP", "Accumule 50 XP aujourd'hui.", 50, "XP_EARNED", 50);
  }

  private LessonModel ensureLesson(String slug, String title, String description, Integer order, String difficulty, String icon, UUID prerequisiteLessonId) {
    LessonModel existing = lessonRepository.findBySlug(slug);
    if (existing != null) {
      return existing;
    }

    LessonModel lesson = new LessonModel();
    lesson.setSlug(slug);
    lesson.setTitle(title);
    lesson.setDescription(description);
    lesson.setSortOrder(order);
    lesson.setDifficulty(difficulty);
    lesson.setIcon(icon);
    lesson.setPrerequisiteLessonId(prerequisiteLessonId);
    lessonRepository.persist(lesson);
    return lesson;
  }

  private void ensureExercise(
      UUID lessonId,
      String slug,
      String title,
      String statement,
      Integer order,
      String difficulty,
      Integer xpReward,
      String exampleInput,
      String expectedOutput,
      String solutionCode) {
    List<ExerciseModel> existing = exerciseRepository.findByLessonId(lessonId);
    ExerciseModel existingExercise = existing.stream()
        .filter(exercise -> slug.equals(exercise.getSlug()))
        .findFirst()
        .orElse(null);
    if (existingExercise != null) {
      existingExercise.setTitle(title);
      existingExercise.setStatementMd(statement);
      existingExercise.setDifficulty(difficulty);
      existingExercise.setXpReward(xpReward);
      existingExercise.setSortOrder(order);
      existingExercise.setExamplesJson(exampleJson(exampleInput, expectedOutput));
      existingExercise.setAllowedLanguages(ALLOWED_LANGUAGES);
      existingExercise.setStarterCodeJson(starterCodeJson(solutionCode));
      ensureTestCase(existingExercise.getId(), expectedOutput);
      return;
    }

    ExerciseModel exercise = new ExerciseModel();
    exercise.setLessonId(lessonId);
    exercise.setSlug(slug);
    exercise.setTitle(title);
    exercise.setStatementMd(statement);
    exercise.setExamplesJson(exampleJson(exampleInput, expectedOutput));
    exercise.setDifficulty(difficulty);
    exercise.setXpReward(xpReward);
    exercise.setSortOrder(order);
    exercise.setAllowedLanguages(ALLOWED_LANGUAGES);
    exercise.setStarterCodeJson(starterCodeJson(solutionCode));
    exerciseRepository.persist(exercise);
    ensureTestCase(exercise.getId(), expectedOutput);
  }

  private void ensureTestCase(UUID exerciseId, String expectedOutput) {
    List<TestCaseModel> existing = testCaseRepository.findByExerciseId(exerciseId);
    if (!existing.isEmpty()) {
      for (TestCaseModel testCase : existing) {
        testCase.setInput("");
        testCase.setExpectedOutput(expectedOutput);
        testCase.setIsHidden(false);
      }
      return;
    }

    TestCaseModel testCase = new TestCaseModel();
    testCase.setExerciseId(exerciseId);
    testCase.setInput("");
    testCase.setExpectedOutput(expectedOutput);
    testCase.setIsHidden(false);
    testCase.setSortOrder(1);
    testCaseRepository.persist(testCase);
  }

  private String starterCodeJson(String solutionCode) {
    return "{\"Python\":\"" + jsonEscape(solutionCode) + "\"}";
  }

  private String exampleJson(String input, String output) {
    return "[{\"input\":\"" + jsonEscape(input) + "\",\"output\":\"" + jsonEscape(output) + "\"}]";
  }

  private String jsonEscape(String value) {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n");
  }

  private void ensureAchievement(String code, String title, String description, String icon, String criteriaType, Integer criteriaValue, Integer xpReward) {
    if (achievementRepository.findByCode(code) != null) {
      return;
    }

    AchievementModel achievement = new AchievementModel();
    achievement.setCode(code);
    achievement.setTitle(title);
    achievement.setDescription(description);
    achievement.setIcon(icon);
    achievement.setCriteriaType(criteriaType);
    achievement.setCriteriaValue(criteriaValue);
    achievement.setXpReward(xpReward);
    achievementRepository.persist(achievement);
  }

  private void ensureQuest(String code, String title, String description, Integer xpReward, String type, Integer criteria) {
    if (questRepository.findByCode(code) != null) {
      return;
    }

    QuestModel quest = new QuestModel();
    quest.setCode(code);
    quest.setTitle(title);
    quest.setDescription(description);
    quest.setXpReward(xpReward);
    quest.setType(type);
    quest.setCriteria(criteria);
    questRepository.persist(quest);
  }
}
