package io.asall.auto.fs;

import static io.asall.auto.fs.JsonUtils.objectMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import io.asall.auto.model.DailyExecution;

public class DailyExecutionsReader implements Function<Path, List<DailyExecution>> {

  @Override
  public List<DailyExecution> apply(Path path) {
    try {
      return objectMapper().readValue(path.toFile(), new TypeReference<>() {
      });
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot read " + path, e);
    }
  }
}