package fr.epita.assistants.ping.presentation.api.response;

import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
public class QuestResponse {
  public UUID id;
  public String code;
  public String title;
  public String description;
  public Integer xpReward;
  public String type;
  public Integer criteria;
  public LocalDate date;
  public Integer progressCurrent;
  public Integer progressTarget;
  public String status;
}
