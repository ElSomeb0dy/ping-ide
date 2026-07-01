package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FSEntryResponse {
	public String name;
	public String path;
	public Boolean isDirectory;
}
