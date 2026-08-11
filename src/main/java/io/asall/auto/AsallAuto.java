package io.asall.auto;

import static java.lang.System.exit;

import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;
import io.asall.auto.action.LoginAction;
import io.asall.auto.action.PutDailyExecutionAction;
import io.asall.auto.action.TerminateAction;
import io.asall.auto.fs.DailyExecutionsReader;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsallAuto {

  private static final String ASA_URL = "https://asa.poja.io";

  public static void main(String[] args) {
    if (args.length == 0) {
      log.error("Usage: Asall <daily-executions.json>");
      exit(1);
    }

    var myDailyExecutions = new DailyExecutionsReader().apply(Path.of(args[0]));
    log.info("Read {} daily execution(s) from {}", myDailyExecutions.size(), args[0]);

    if (myDailyExecutions.isEmpty()) {
      log.error("No daily executions found");
      exit(0);
    }

    try (var playwright = Playwright.create()) {
      var browser =
        playwright.chromium().launch(new LaunchOptions().setHeadless(false));
      var page = browser.newPage();

      page.navigate(ASA_URL);

      var isLoggedIn = new LoginAction().apply(page, null);
      if (!isLoggedIn) {
        log.info("Failed to login.");
        exit(1);
        return;
      }

      myDailyExecutions
        .stream()
        .dropWhile(
          e -> new PutDailyExecutionAction().apply(
            page, e
          )
        )
        .forEach(
          e -> log.info("Failed to put daily execution: {}", e)
        );

      new TerminateAction().apply(page, browser);
    }
  }
}