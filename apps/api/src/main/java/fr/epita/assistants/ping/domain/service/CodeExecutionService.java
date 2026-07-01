package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.utils.HttpError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CodeExecutionService {
  private final Semaphore semaphore;

  @ConfigProperty(name = "ping.execution.timeout-seconds", defaultValue = "5")
  Long timeoutSeconds;

  @ConfigProperty(name = "ping.execution.memory", defaultValue = "128m")
  String memoryLimit;

  @ConfigProperty(name = "ping.execution.cpus", defaultValue = "0.5")
  String cpuLimit;

  @ConfigProperty(name = "ping.execution.workdir", defaultValue = "/tmp/ping-exec")
  String executionWorkdir;

  public CodeExecutionService(@ConfigProperty(name = "ping.execution.max-concurrent", defaultValue = "2") Integer maxConcurrent) {
    semaphore = new Semaphore(maxConcurrent);
  }

  public ExecutionResult run(String language, String code, String stdin) {
    RuntimeSpec spec = specFor(language);
    Path tempDir = null;
    boolean acquired = false;
    try {
      semaphore.acquire();
      acquired = true;
      Path workdir = Path.of(executionWorkdir).toAbsolutePath().normalize();
      Files.createDirectories(workdir);
      tempDir = Files.createTempDirectory(workdir, "run-" + UUID.randomUUID() + "-");
      Files.writeString(tempDir.resolve(spec.fileName()), code == null ? "" : code, StandardCharsets.UTF_8);

      Process process = new ProcessBuilder(dockerCommand(spec, tempDir))
          .redirectErrorStream(false)
          .start();

      if (stdin != null && !stdin.isEmpty()) {
        process.getOutputStream().write(stdin.getBytes(StandardCharsets.UTF_8));
      }
      process.getOutputStream().close();

      CompletableFuture<String> stdout = readAsync(process.getInputStream());
      CompletableFuture<String> stderr = readAsync(process.getErrorStream());
      boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

      if (!finished) {
        process.destroyForcibly();
        return new ExecutionResult("", "Execution timed out", -1, true);
      }

      return new ExecutionResult(stdout.join(), stderr.join(), process.exitValue(), false);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new HttpError(Response.Status.INTERNAL_SERVER_ERROR, "Execution interrupted").get();
    } catch (IOException e) {
      throw new HttpError(Response.Status.INTERNAL_SERVER_ERROR, "Could not execute code: " + e.getMessage()).get();
    } finally {
      if (acquired) {
        semaphore.release();
      }
      if (tempDir != null) {
        deleteRecursively(tempDir);
      }
    }
  }

  private List<String> dockerCommand(RuntimeSpec spec, Path tempDir) {
    return List.of(
        "docker",
        "run",
        "--rm",
        "-i",
        "--network",
        "none",
        "--memory",
        memoryLimit,
        "--cpus",
        cpuLimit,
        "--pids-limit",
        "64",
        "-v",
        tempDir.toAbsolutePath() + ":/sandbox",
        "-w",
        "/sandbox",
        spec.image(),
        "sh",
        "-c",
        spec.command());
  }

  private RuntimeSpec specFor(String language) {
    if (!"Python".equals(language)) {
      throw new HttpError(Response.Status.BAD_REQUEST, "Unsupported language").get();
    }
    return new RuntimeSpec("python:3.12-slim", "main.py", "python3 main.py");
  }

  private CompletableFuture<String> readAsync(java.io.InputStream inputStream) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        return "";
      }
    });
  }

  private void deleteRecursively(Path path) {
    try (var walk = Files.walk(path)) {
      walk.sorted((left, right) -> right.compareTo(left)).forEach(child -> {
        try {
          Files.deleteIfExists(child);
        } catch (IOException ignored) {
        }
      });
    } catch (IOException ignored) {
    }
  }

  private record RuntimeSpec(String image, String fileName, String command) {
  }

  public record ExecutionResult(String stdout, String stderr, Integer exitCode, Boolean timedOut) {
  }
}
