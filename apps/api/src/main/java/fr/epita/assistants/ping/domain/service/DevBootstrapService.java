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
    prewarmExecutionImage();

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

  private void prewarmExecutionImage() {
    new Thread(() -> {
      try {
        Logger.log("Pulling python:3.12-slim execution image...");
        new ProcessBuilder("docker", "pull", "python:3.12-slim")
                .inheritIO()
                .start()
                .waitFor();
        Logger.log("Execution image ready.");
      } catch (Exception e) {
        Logger.error("Failed to prewarm execution image: " + e.getMessage());
      }
    }, "image-prewarm").start();
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
    ensureExercise(
            arrays.getId(),
            "second-largest",
            "Deuxième plus grand",
            "Trouve la deuxième plus grande valeur distincte d'un tableau. La réponse est préremplie.",
            4,
            "Facile",
            25,
            "nums = [4, 2, 15, 8, 15]",
            "8",
            "print(8)\n");
    ensureExercise(
            arrays.getId(),
            "sum-array",
            "Somme d'un tableau",
            "Calcule la somme de tous les éléments d'un tableau. La solution de test est déjà écrite.",
            5,
            "Facile",
            20,
            "nums = [1, 2, 3, 4]",
            "10",
            "print(10)\n");
    ensureExercise(
            arrays.getId(),
            "rotate-array",
            "Rotation de tableau",
            "Décale les éléments d'un tableau de k positions vers la droite. La solution de test est déjà écrite.",
            6,
            "Moyen",
            35,
            "nums = [1, 2, 3, 4, 5], k = 2",
            "[4, 5, 1, 2, 3]",
            "print(\"[4, 5, 1, 2, 3]\")\n");

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
    ensureExercise(
            loops.getId(),
            "is-prime",
            "Nombre premier",
            "Détermine si un nombre est premier à l'aide d'une boucle. La solution de test est déjà écrite.",
            3,
            "Moyen",
            30,
            "n = 13",
            "True",
            "print(True)\n");
    ensureExercise(
            loops.getId(),
            "gcd",
            "PGCD",
            "Calcule le plus grand commun diviseur de deux nombres avec une boucle. La solution de test est déjà écrite.",
            4,
            "Moyen",
            35,
            "a = 48, b = 18",
            "6",
            "print(6)\n");

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
    ensureExercise(
            recursion.getId(),
            "fibonacci",
            "Suite de Fibonacci",
            "Calcule le n-ième terme de la suite de Fibonacci de façon récursive. La solution de test est déjà écrite.",
            2,
            "Moyen",
            40,
            "n = 7",
            "13",
            "def fibonacci(n):\n    if n <= 1:\n        return n\n    return fibonacci(n - 1) + fibonacci(n - 2)\n\nprint(fibonacci(7))\n");
    ensureExercise(
            recursion.getId(),
            "hanoi-moves",
            "Tours de Hanoï",
            "Compte le nombre minimal de mouvements pour résoudre les tours de Hanoï avec n disques, de façon récursive. La solution de test est déjà écrite.",
            3,
            "Difficile",
            50,
            "n = 4",
            "15",
            "def hanoi(n):\n    if n == 0:\n        return 0\n    return 2 * hanoi(n - 1) + 1\n\nprint(hanoi(4))\n");

    LessonModel strings = ensureLesson(
            "strings-basics",
            "Manipulation de chaînes",
            "Découvre les techniques essentielles pour manipuler du texte.",
            4,
            "Facile",
            "Chaînes",
            recursion.getId());
    ensureExercise(
            strings.getId(),
            "is-palindrome",
            "Palindrome",
            "Vérifie si une chaîne se lit de la même façon dans les deux sens. La solution de test est déjà écrite.",
            1,
            "Facile",
            25,
            "s = \"kayak\"",
            "True",
            "print(True)\n");
    ensureExercise(
            strings.getId(),
            "count-vowels",
            "Compter les voyelles",
            "Compte le nombre de voyelles dans une chaîne de caractères. La réponse est préremplie.",
            2,
            "Facile",
            25,
            "s = \"epita\"",
            "3",
            "print(3)\n");
    ensureExercise(
            strings.getId(),
            "word-frequency",
            "Mot le plus fréquent",
            "Trouve le mot qui apparaît le plus souvent dans une phrase. La solution de test est déjà écrite.",
            3,
            "Moyen",
            35,
            "s = \"un chat un chien un chat\"",
            "chat",
            "print(\"chat\")\n");
    ensureExercise(
            strings.getId(),
            "anagram-check",
            "Anagrammes",
            "Vérifie si deux chaînes sont des anagrammes l'une de l'autre. La solution de test est déjà écrite.",
            4,
            "Difficile",
            45,
            "a = \"ecole\", b = \"coele\"",
            "True",
            "print(True)\n");

    LessonModel dictionaries = ensureLesson(
            "dictionaries-basics",
            "Dictionnaires",
            "Manipule des paires clé-valeur pour résoudre des problèmes de comptage et de lookup.",
            5,
            "Moyen",
            "Structures",
            strings.getId());
    ensureExercise(
            dictionaries.getId(),
            "char-count",
            "Compter les caractères",
            "Construis un dictionnaire comptant les occurrences de chaque caractère. La solution de test est déjà écrite.",
            1,
            "Facile",
            30,
            "s = \"abac\"",
            "{'a': 2, 'b': 1, 'c': 1}",
            "print({'a': 2, 'b': 1, 'c': 1})\n");
    ensureExercise(
            dictionaries.getId(),
            "merge-dicts",
            "Fusionner deux dictionnaires",
            "Fusionne deux dictionnaires en additionnant les valeurs des clés communes. La solution de test est déjà écrite.",
            2,
            "Moyen",
            35,
            "a = {'x': 1, 'y': 2}, b = {'y': 3, 'z': 4}",
            "{'x': 1, 'y': 5, 'z': 4}",
            "print({'x': 1, 'y': 5, 'z': 4})\n");
    ensureExercise(
            dictionaries.getId(),
            "group-by-length",
            "Grouper par longueur",
            "Regroupe une liste de mots dans un dictionnaire selon leur longueur. La solution de test est déjà écrite.",
            3,
            "Difficile",
            45,
            "words = [\"a\", \"bb\", \"cc\", \"ddd\"]",
            "{1: ['a'], 2: ['bb', 'cc'], 3: ['ddd']}",
            "print({1: ['a'], 2: ['bb', 'cc'], 3: ['ddd']})\n");

    LessonModel sorting = ensureLesson(
            "sorting-searching",
            "Tri et recherche",
            "Implémente des algorithmes classiques de tri et de recherche.",
            6,
            "Moyen",
            "Algorithmie",
            dictionaries.getId());
    ensureExercise(
            sorting.getId(),
            "bubble-sort",
            "Tri à bulles",
            "Implémente le tri à bulles pour trier une liste de nombres. La solution de test est déjà écrite.",
            1,
            "Moyen",
            35,
            "nums = [5, 2, 9, 1, 5]",
            "[1, 2, 5, 5, 9]",
            "print([1, 2, 5, 5, 9])\n");
    ensureExercise(
            sorting.getId(),
            "binary-search",
            "Recherche binaire",
            "Trouve l'indice d'une valeur dans une liste triée en recherche binaire. La solution de test est déjà écrite.",
            2,
            "Moyen",
            40,
            "nums = [1, 3, 5, 7, 9, 11], target = 7",
            "3",
            "print(3)\n");
    ensureExercise(
            sorting.getId(),
            "kth-largest",
            "K-ième plus grand élément",
            "Trouve le k-ième plus grand élément d'une liste sans trier entièrement à la main. La solution de test est déjà écrite.",
            3,
            "Difficile",
            50,
            "nums = [3, 2, 1, 5, 6, 4], k = 2",
            "5",
            "print(5)\n");

    LessonModel advanced = ensureLesson(
            "advanced-challenges",
            "Défis avancés",
            "Des problèmes plus corsés pour tester ta maîtrise des structures et de l'algorithmique.",
            7,
            "Difficile",
            "Défis",
            sorting.getId());
    ensureExercise(
            advanced.getId(),
            "longest-substring",
            "Plus longue sous-chaîne sans répétition",
            "Trouve la longueur de la plus longue sous-chaîne sans caractère répété. La solution de test est déjà écrite.",
            1,
            "Difficile",
            55,
            "s = \"abcabcbb\"",
            "3",
            "print(3)\n");
    ensureExercise(
            advanced.getId(),
            "matrix-rotate",
            "Rotation de matrice",
            "Fais pivoter une matrice carrée de 90 degrés sur elle-même, en place. La solution de test est déjà écrite.",
            2,
            "Difficile",
            55,
            "matrix = [[1,2],[3,4]]",
            "[[3, 1], [4, 2]]",
            "print([[3, 1], [4, 2]])\n");
    ensureExercise(
            advanced.getId(),
            "coin-change",
            "Rendu de monnaie",
            "Trouve le nombre minimal de pièces pour atteindre un montant donné (programmation dynamique). La solution de test est déjà écrite.",
            3,
            "Difficile",
            60,
            "coins = [1, 3, 4], amount = 6",
            "2",
            "print(2)\n");
    ensureExercise(
            advanced.getId(),
            "valid-parentheses",
            "Parenthèses valides",
            "Vérifie si une chaîne de parenthèses/crochets/accolades est correctement équilibrée, avec une pile. La solution de test est déjà écrite.",
            4,
            "Difficile",
            50,
            "s = \"{[()()]}\"",
            "True",
            "print(True)\n");
    ensureExercise(
            advanced.getId(),
            "longest-increasing-subsequence",
            "Plus longue sous-suite croissante",
            "Trouve la longueur de la plus longue sous-suite strictement croissante d'une liste. La solution de test est déjà écrite.",
            5,
            "Difficile",
            65,
            "nums = [10, 9, 2, 5, 3, 7, 101, 18]",
            "4",
            "print(4)\n");

    ensureAchievement("FIRST_EXERCISE", "Premier pas", "Termine ton tout premier exercice.", "/achievements/first-exercise.svg", "EXERCISES_COMPLETED", 1, 30);
    ensureAchievement("FIVE_EXERCISES", "Marathonien junior", "Termine 5 exercices au total.", "/achievements/five-exercises.svg", "EXERCISES_COMPLETED", 5, 50);
    ensureAchievement("TEN_EXERCISES", "Habitué", "Termine 10 exercices au total.", "/achievements/ten-exercises.svg", "EXERCISES_COMPLETED", 10, 60);
    ensureAchievement("TWENTYFIVE_EXERCISES", "Expert des exercices", "Termine 25 exercices au total.", "/achievements/twentyfive-exercises.svg", "EXERCISES_COMPLETED", 25, 100);
    ensureAchievement("FIRST_LESSON", "Leçon maîtrisée", "Termine une leçon complète.", "/achievements/first-lesson.svg", "LESSONS_COMPLETED", 1, 50);
    ensureAchievement("THREE_LESSONS", "Étudiant assidu", "Termine 3 leçons complètes.", "/achievements/three-lessons.svg", "LESSONS_COMPLETED", 3, 75);
    ensureAchievement("ALL_LESSONS", "Maître du parcours", "Termine toutes les leçons disponibles.", "/achievements/all-lessons.svg", "LESSONS_COMPLETED", 7, 150);
    ensureAchievement("LEVEL_5", "Niveau 5", "Atteins le niveau 5.", "/achievements/level-5.svg", "LEVEL", 5, 75);
    ensureAchievement("LEVEL_10", "Niveau 10", "Atteins le niveau 10.", "/achievements/level-10.svg", "LEVEL", 10, 120);
    ensureAchievement("LEVEL_20", "Niveau 20", "Atteins le niveau 20.", "/achievements/level-20.svg", "LEVEL", 20, 200);
    ensureAchievement("LEVEL_25", "Niveau 25", "Atteins le niveau 25.", "/achievements/level-25.svg", "LEVEL", 25, 250);
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
    AchievementModel existing = achievementRepository.findByCode(code);
    if (existing != null) {
      existing.setTitle(title);
      existing.setDescription(description);
      existing.setIcon(icon);
      existing.setCriteriaType(criteriaType);
      existing.setCriteriaValue(criteriaValue);
      existing.setXpReward(xpReward);
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
    QuestModel existing = questRepository.findByCode(code);
    if (existing != null) {
      existing.setTitle(title);
      existing.setDescription(description);
      existing.setXpReward(xpReward);
      existing.setType(type);
      existing.setCriteria(criteria);
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