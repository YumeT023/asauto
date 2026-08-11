package io.asall.auto.model;

import static java.lang.Float.parseFloat;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record DailyExecution(
  LocalDate date,
  List<Task> tasks
) {
  private static final int MAX_DECIMALS = 1;
  private static final int SCALE = (int) pow(10, MAX_DECIMALS);

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
    public Task {
      var sj = new StringJoiner(".\n");
      if (score == null || score.trim().isEmpty()) {
        sj.add(
          "Task score is required"
        );
      }
      if (category == null || category.trim().isEmpty()) {

        sj.add(
          "Task category is required"
        );
      }
      if (description == null || description.trim().isEmpty()) {
        sj.add(
          "Task description is required"
        );
      }

      if (sj.length() > 0) {
        throw new IllegalArgumentException(sj.toString());
      }
    }


    int scaledScore() {
      float parsed;
      try {
        parsed = parseFloat(score.trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Task score is not a number: " + score);
      }

      if (parsed < 0) {
        throw new IllegalArgumentException("Task score must be positive, got " + score);
      }

      return round(parsed * SCALE);
    }

    public String scoreAsString() {
      return String.valueOf(scaledScore() / (float) SCALE);
    }
  }
}