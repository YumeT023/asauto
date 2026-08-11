package io.asall.auto;

import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;
import io.asall.auto.action.TerminateAction;

public class AsallAuto {
  private static final String ASA_URL = "https://asa.poja.io";

  public static void main(String[] args) {
    try (var playwright = Playwright.create()) {
      var browser =
        playwright.chromium().launch(new LaunchOptions().setHeadless(false));
      var page = browser.newPage();

      page.navigate(ASA_URL);

      new TerminateAction().apply(page, browser);
    }
  }
}