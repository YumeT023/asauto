package io.asall.auto;

import static java.lang.System.exit;

import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;
import io.asall.auto.action.LoginAction;
import io.asall.auto.action.TerminateAction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsallAuto {
  private static final String ASA_URL = "https://asa.poja.io";

  public static void main(String[] args) {
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

      new TerminateAction().apply(page, browser);
    }
  }
}