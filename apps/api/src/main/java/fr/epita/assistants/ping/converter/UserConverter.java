package fr.epita.assistants.ping.converter;

import java.util.ArrayList;
import java.util.List;

import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.presentation.api.response.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserConverter {
  public UserResponse toUserResponse(UserModel userModel) {
    return new UserResponse(
        userModel.getId(),
        userModel.getLogin(),
        userModel.getDisplayName(),
        userModel.getIsAdmin(),
        userModel.getAvatar());
  }

  public List<UserResponse> toResponseList(List<UserModel> userModels) {
    List<UserResponse> responseList = new ArrayList<>();

    for (UserModel userModel : userModels) {
      responseList.add(toUserResponse(userModel));
    }

    return responseList;
  }
}
