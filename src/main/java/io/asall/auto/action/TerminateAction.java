package io.asall.auto.action;

import static java.lang.Thread.sleep;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TerminateAction implements Action<Browser> {
  @Override
  public boolean apply(Page page, Browser browser) {
    log.info("Done. Press Enter here to close.");

    if (!waitForEnter()) {
      keepAlive();
    }

    if (browser.isConnected()) {
      browser.close();
    }

    return true;
  }

  private static boolean waitForEnter() {
    var stdin = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      String line;
      try {
        line = stdin.readLine();
      } catch (IOException e) {
        log.debug("Could not read from stdin", e);
        return false;
      }
      if (line == null) {
        log.debug("stdin reached end of input");
        return false;
      }
      if (line.isEmpty()) {
        return true;
      }
      log.info("Press Enter with nothing typed before it to close.");
    }
  }

  private static void keepAlive() {
    log.warn("No Enter can be read from this terminal, staying alive. Stop with Ctrl+C.");
    while (true) {
      try {
        sleep(1_000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }
  }
}