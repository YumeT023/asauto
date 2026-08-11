package io.asall.auto.action;

import static java.util.Arrays.stream;
import static lombok.AccessLevel.PRIVATE;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.WaitForConditionOptions;
import com.microsoft.playwright.PlaywrightException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@NoArgsConstructor(access = PRIVATE)
@Slf4j
public final class LoginAction implements Action<Object> {

  private static final double MANUAL_LOGIN_TIMEOUT_MS = 300_000;
  private static final String[] AUTHENTICATED_MENUS = {
    "Mission Explorer", "Work & Care Calendar", "Daily Execution", "Contracts", "Invoices"
  };

  private static final Action<?> INSTANCE = new LoginAction();

  public static boolean login(Page page, Object ignored) {
    return INSTANCE.apply(page, null);
  }

  @Override
  public boolean apply(Page page, Object ignored) {
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