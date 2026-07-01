package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.model.FolderModel;
import fr.epita.assistants.ping.utils.HttpError;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

@ApplicationScoped
public class FolderService {

  @ConfigProperty(name = "FILESYSTEM_DEFAULT_PATH")
  String defaultPath;

  public List<FolderModel> fetchFolders(String path) {
    if (path == null) {
      path = "";
    }

    var root = Paths.get(defaultPath);
    var complet = root.resolve(path).normalize();

    if (!complet.startsWith(root)) {
      throw new HttpError(Response.Status.FORBIDDEN,
          "Path traversal attack detected").get();
    }

    if (!Files.exists(complet) || !Files.isDirectory(complet)) {
      throw new HttpError(Response.Status.NOT_FOUND,
          "The folder could not be found").get();
    }

    return listChildren(complet, root);
  }

  public void createFolder(String path) {
    if (path == null || path.isBlank()) {
      throw new HttpError(Response.Status.BAD_REQUEST,
          "The relative path is invalid").get();
    }

    var root = Paths.get(defaultPath);
    var complet = root.resolve(path).normalize();

    if (!complet.startsWith(root)) {
      throw new HttpError(Response.Status.FORBIDDEN,
          "Path traversal attack detected").get();
    }

    if (Files.exists(complet)) {
      throw new HttpError(Response.Status.CONFLICT,
          "The folder already exists").get();
    }

    createFolder(complet);
  }

  public void deleteFolder(String path) {
    if (path == null || path.isBlank()) {
      path = "";
    }

    var root = Paths.get(defaultPath);
    var complet = root.resolve(path).normalize();

    if (!complet.startsWith(root)) {
      throw new HttpError(Response.Status.FORBIDDEN,
          "Path traversal attack detected").get();
    }

    if (!Files.exists(complet) || !Files.isDirectory(complet)) {
      throw new HttpError(Response.Status.NOT_FOUND,
          "The folder could not be found").get();
    }

    deleteFolder(complet, root);
  }

  public void moveFolder(String src, String dst) {

    if (src == null || src.isBlank() || dst == null || dst.isBlank()) {
      throw new HttpError(Response.Status.BAD_REQUEST, "The relative path is invalid").get();
    }

    Path root = Paths.get(defaultPath).toAbsolutePath().normalize();

    Path source = root.resolve(src).normalize();
    Path destination = root.resolve(dst).normalize();

    if (Paths.get(src).isAbsolute() || Paths.get(dst).isAbsolute()
        || !source.startsWith(root) || !destination.startsWith(root)) {
      throw new HttpError(Response.Status.FORBIDDEN, "Path traversal attack detected").get();
    }

    if (!Files.exists(source) || !Files.isDirectory(source)) {
      throw new HttpError(Response.Status.NOT_FOUND, "Source folder not found").get();
    }

    if (Files.exists(destination)) {
      throw new HttpError(Response.Status.CONFLICT, "Destination folder already exists").get();
    }

    try {
      Files.move(source, destination);
    } catch (IOException e) {
      throw new HttpError(Response.Status.INTERNAL_SERVER_ERROR, "Could not move the folder").get();
    }
  }

  private List<FolderModel> listChildren(Path directory, Path root) {
    List<FolderModel> folderModels = new ArrayList<>();

    try (Stream<Path> children = Files.list(directory)) {
      for (Path child : (Iterable<Path>) children::iterator) {
        folderModels.add(new FolderModel(
            child.getFileName().toString(),
            root.relativize(child).toString(),
            Files.isDirectory(child)));
      }
    } catch (IOException e) {
      throw new HttpError(Response.Status.INTERNAL_SERVER_ERROR, "Failed to list the folder")
          .get();
    }
    return folderModels;
  }

  private void createFolder(Path target) {
    try {
      Files.createDirectories(target);
    } catch (IOException e) {
      throw new HttpError(Response.Status.INTERNAL_SERVER_ERROR,
          "Failed to create the folder").get();
    }
  }

  private void deleteFolder(Path target, Path root) {
    try {
      if (target.equals(root)) {
        try (Stream<Path> children = Files.list(target)) {
          for (Path child : (Iterable<Path>) children::iterator) {
            deleteRecursively(child);
          }
        }
      } else {
        deleteRecursively(target);
      }
    } catch (IOException e) {
      throw new HttpError(Response.Status.INTERNAL_SERVER_ERROR,
          "Failed to delete the folder").get();
    }
  }

  private void deleteRecursively(Path path) throws IOException {
    try (Stream<Path> walk = Files.walk(path)) {
      walk.sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }
  }
}
