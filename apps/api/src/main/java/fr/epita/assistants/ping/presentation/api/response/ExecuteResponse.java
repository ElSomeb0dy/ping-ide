package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExecuteResponse {
  public String stdout;
  public String stderr;
  public Integer exitCode;
  public Boolean timedOut;
}
