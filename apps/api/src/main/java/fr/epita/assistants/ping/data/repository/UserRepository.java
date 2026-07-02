package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.UserModel;

import java.util.List;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserModel> {

  @Transactional
  public void createUser(UserModel newUser) {
    persist(newUser);
  }

  public UserModel findByLogin(String login) {
    return find("login", login).firstResult();
  }

  public UserModel findByEmail(String email) {
    return find("email", email).firstResult();
  }

  public List<UserModel> fetchAll() {
    return listAll();
  }

  public UserModel findById(UUID id) {
    return find("id", id).firstResult();
  }

  public Long deleteById(UUID id) {
    return delete("id", id);
  }
}
