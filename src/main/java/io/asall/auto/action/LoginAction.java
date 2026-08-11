package io.asall.auto.action;

import static java.util.Arrays.stream;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.WaitForConditionOptions;
import com.microsoft.playwright.PlaywrightException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LoginAction implements Action<Object> {

  static final double MANUAL_LOGIN_TIMEOUT_MS = 300_000;
  static final String[] AUTHENTICATED_MENUS = {
    "Mission Explorer", "Work & Care Calendar", "Daily Execution", "Contracts", "Invoices"
  };

  @Override
  public boolean apply(Page page, Object obj) {
    log.info("Waiting for you to fill in and submit the login form...");
    page.waitForCondition(
      () -> isConnected(page),
      new WaitForConditionOptions().setTimeout(MANUAL_LOGIN_TIMEOUT_MS));
    return true;
  }

  private static boolean isConnected(Page page) {
    try {
      return stream(AUTHENTICATED_MENUS).allMatch(
        m -> page.getByText(m).first().isVisible()
      );
    } catch (PlaywrightException e) {
      return false;
    }
  }
}