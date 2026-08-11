package io.asall.auto.action;

import com.microsoft.playwright.Page;

public sealed interface Action<T>
  permits LoginAction, PutDailyExecutionAction, TerminateAction {
  boolean apply(Page page, T context);
}