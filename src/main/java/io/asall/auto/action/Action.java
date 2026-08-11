package io.asall.auto.action;

import com.microsoft.playwright.Page;

@FunctionalInterface
public interface Action<T> {
  boolean apply(Page page, T context);
}