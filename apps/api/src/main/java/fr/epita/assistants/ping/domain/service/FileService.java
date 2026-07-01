package fr.epita.assistants.ping.domain.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.epita.assistants.ping.utils.HttpError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class FileService {
  @ConfigProperty(name = "FILESYSTEM_DEFAULT_PATH", defaultValue = "/tmp/ping")
  String fsRoot;

  public Path checkPath(String relativePath) {

    if (relativePath == null || relativePath.isBlank()) {
      throw new HttpError(Status.BAD_REQUEST, "Path is invalid").get();
    }

    Path root = rootPath();

    String cleanPath = relativePath;
    while (cleanPath.startsWith("/") || cleanPath.startsWith("\\")) {
      cleanPath = cleanPath.substring(1);
    }

    Path resolved = root.resolve(relativePath).normalize();

    if (!resolved.startsWith(root)) {
      throw new HttpError(Status.FORBIDDEN, "Possible path traversal attack").get();
    }

    return resolved;
  }

  public byte[] getFileContent(String relativePath) {

    Path target = checkPath(relativePath);

    if (!Files.exists(target) || Files.isDirectory(target)) {
      throw new HttpError(Status.NOT_FOUND, "File not found").get();
    }

    try {
      return Files.readAllBytes(target);
    } catch (IOException e) {
      throw new HttpError(Status.INTERNAL_SERVER_ERROR, "Could not read the file").get();
    }
  }

  public void createFile(String relativePath) {

    Path target = checkPath(relativePath);

    if (Files.exists(target)) {
      throw new HttpError(Status.CONFLICT, "The file already exists").get();
    }

    try {
      Files.createDirectories(target.getParent());
      Files.createFile(target);
    } catch (IOException e) {
      throw new HttpError(Status.INTERNAL_SERVER_ERROR, "Could not create the file").get();
    }
  }

  public void deleteFile(String relativePath) {

    Path target = checkPath(relativePath);

    if (!Files.exists(target)) {
      throw new HttpError(Status.NOT_FOUND, "File not found").get();
    }

    Path root = rootPath();

    try {
      if (target.equals(root)) {
        try (var children = Files.list(root)) {

          for (Path c : children.toList()) {
            recDelete(c);
          }
        }
      } else {
        recDelete(target);
      }
    } catch (IOException e) {
      throw new HttpError(Status.INTERNAL_SERVER_ERROR, "Could not delete the file").get();
    }
  }

  public void moveFile(String src, String dst) {

    Path source = checkPath(src);
    Path destination = checkPath(dst);

    if (!Files.exists(source) || Files.isDirectory(source)) {
      throw new HttpError(Status.NOT_FOUND, "Source file not found").get();
    }

    if (Files.exists(destination)) {
      throw new HttpError(Status.CONFLICT, "Destination file already exists").get();
    }

    try {
      Files.createDirectories(destination.getParent());
      Files.move(source, destination);
    } catch (IOException e) {
      throw new HttpError(Status.INTERNAL_SERVER_ERROR, "Could not move the file").get();
    }
  }

  public void uploadFile(String relativePath, byte[] content) {

    Path target = checkPath(relativePath);

    try {
      Files.createDirectories(target.getParent());
      Files.write(target, content);
    } catch (IOException e) {
      throw new HttpError(Status.INTERNAL_SERVER_ERROR, "Could not upload the file").get();
    }
  }

  private void recDelete(Path path) throws IOException {

    if (Files.isDirectory(path)) {
      try (var children = Files.list(path)) {
        for (Path c : children.toList()) {
          recDelete(c);
        }
      }
    }

    Files.delete(path);
  }

  private Path rootPath() {
    return Paths.get(fsRoot).toAbsolutePath().normalize();
  }
}
