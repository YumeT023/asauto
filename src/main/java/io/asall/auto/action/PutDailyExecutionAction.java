package io.asall.auto.action;

import static com.microsoft.playwright.options.AriaRole.BUTTON;
import static com.microsoft.playwright.options.AriaRole.LINK;
import static com.microsoft.playwright.options.LoadState.NETWORKIDLE;
import static com.microsoft.playwright.options.WaitForSelectorState.VISIBLE;
import static lombok.AccessLevel.PRIVATE;

import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.TimeoutError;
import io.asall.auto.model.DailyExecution;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = PRIVATE)
@Slf4j
public final class PutDailyExecutionAction implements Action<DailyExecution> {

  private static final Action<DailyExecution> INSTANCE = new PutDailyExecutionAction();

  private static final String DAILY_EXECUTION_MENU = "Daily Execution";
  private static final String DAILY_EXECUTION_HREF = "/daily-execution";
  private static final int FORM_ROWS = 5;
  private static final String ERROR_TEXT = "Loza! Oops...";
  private static final double ERROR_TIMEOUT_MS = 3_000;

  @Override
  public boolean apply(Page page, DailyExecution ctx) {
    var tasks = ctx.tasks();
    if (tasks.size() > FORM_ROWS) {
      log.error("Form holds {} rows but got {} tasks: {}", FORM_ROWS, tasks.size(), ctx);
      return false;
    }

    page.getByRole(LINK, new GetByRoleOptions().setName(DAILY_EXECUTION_MENU)).click();

    fillDate(page, ctx.date().toString());
    for (var row = 0; row < tasks.size(); row++) {
      fillTask(page, row + 1, tasks.get(row));
    }

    page.waitForResponse(
      r -> r.url().endsWith(DAILY_EXECUTION_HREF) && "POST".equals(r.request().method()),
      () -> page.getByRole(BUTTON, new GetByRoleOptions().setName("Submit")).click());
    page.waitForLoadState(NETWORKIDLE);

    var isSuccess = !isErrorShown(page);
    log.info("Submitted {}: code {}", ctx.date(), isSuccess ? 1 : 0);
    return isSuccess;
  }

  private static boolean isErrorShown(Page page) {
    try {
      page.getByText(ERROR_TEXT)
        .first()
        .waitFor(new WaitForOptions().setState(VISIBLE).setTimeout(ERROR_TIMEOUT_MS));
      return true;
    } catch (TimeoutError e) {
      return false;
    }
  }

  /**
   * The date input is readonly and driven by flatpickr, so fill() would be rejected.
   */
  private static void fillDate(Page page, String isoDate) {
    page.locator("#date")
      .evaluate("(input, date) => input._flatpickr.setDate(date, true)", isoDate);
  }

  private static void fillTask(Page page, int row, DailyExecution.Task task) {
    page.selectOption("#mission-code-" + row, task.category());
    page.fill("#mission-percentage-" + row, task.scoreAsString());
    page.fill("#mission-comment-" + row, task.description());
  }

  public static boolean putDailyExecution(Page page, DailyExecution execution) {
    return INSTANCE.apply(page, execution);
  }
}