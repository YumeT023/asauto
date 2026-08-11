package io.asall.auto.fs;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    OBJECT_MAPPER.configure(WRITE_DATES_AS_TIMESTAMPS, false);
    OBJECT_MAPPER.findAndRegisterModules();
  }

  public static ObjectMapper objectMapper() {
    return OBJECT_MAPPER;
  }
}