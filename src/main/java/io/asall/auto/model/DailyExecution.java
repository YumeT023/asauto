package io.asall.auto.model;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record DailyExecution(
  LocalDate date,
  List<Task> tasks
) {
  private static final int MAX_DECIMALS = 1;
  private static final int SCALE = (int) Math.pow(10, MAX_DECIMALS);

  public DailyExecution {
    var total = tasks.stream()
      .mapToInt(Task::scaledScore)
      .sum();

    if (total != SCALE) {
      log.warn("Task scores must total exactly 1, got {}", total / (float) SCALE);
    }
  }

  public record Task(
    String score,
    String category,
    String description
  ) {


    int scaledScore() {
      float parsed;
      try {
        parsed = Float.parseFloat(score.trim());
      } catch (NullPointerException | NumberFormatException e) {
        throw new IllegalArgumentException("Task score is not a number: " + score);
      }

      if (parsed < 0) {
        throw new IllegalArgumentException("Task score must be positive, got " + score);
      }

      return Math.round(parsed * SCALE);
    }

    public String scoreAsString() {
      return String.valueOf(scaledScore() / (float) SCALE);
    }
  }
}