package io.asall.auto.action;

import static java.lang.Thread.currentThread;
import static lombok.AccessLevel.PRIVATE;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = PRIVATE)
@Slf4j
public final class TerminateAction implements Action<Browser> {

  private static final Action<Browser> INSTANCE = new TerminateAction();

  public static boolean terminate(Page page, Browser browser) {
    return INSTANCE.apply(page, browser);
  }

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
    try {
      new CountDownLatch(1).await();
    } catch (InterruptedException e) {
      currentThread().interrupt();
    }
  }
}