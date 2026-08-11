package io.asall.auto;

import static io.asall.auto.action.LoginAction.login;
import static io.asall.auto.action.PutDailyExecutionAction.putDailyExecution;
import static io.asall.auto.action.TerminateAction.terminate;
import static java.lang.System.exit;

import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;
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

      var isLoggedIn = login(page, null);
      if (!isLoggedIn) {
        log.info("Failed to login.");
        exit(1);
        return;
      }

      myDailyExecutions
        .stream()
        .dropWhile(
          d -> putDailyExecution(page, d)
        )
        .forEach(
          d -> log.info("Failed to put daily execution: {}", d)
        );

      terminate(page, browser);
    }
  }
}