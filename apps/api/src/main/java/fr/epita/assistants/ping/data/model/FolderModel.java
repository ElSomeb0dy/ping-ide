package fr.epita.assistants.ping.data.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class FolderModel {
	public String name;
	public String path;
	public Boolean isDirectory;
}
