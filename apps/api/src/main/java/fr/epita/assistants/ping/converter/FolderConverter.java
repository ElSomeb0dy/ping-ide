package fr.epita.assistants.ping.converter;

import fr.epita.assistants.ping.data.model.FolderModel;
import fr.epita.assistants.ping.presentation.api.response.FSEntryResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FolderConverter {

	public List<FSEntryResponse> toResponseList(List<FolderModel> folderModels) {
		List<FSEntryResponse> responses = new ArrayList<>();

		for (FolderModel folderModel : folderModels) {
			responses.add(new FSEntryResponse(
					folderModel.name,
					folderModel.path,
					folderModel.isDirectory
			));
		}
		return responses;
	}
}
