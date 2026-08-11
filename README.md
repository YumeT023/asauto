# asall

Fills the daily execution form on [asa.poja.io](https://asa.poja.io) from a JSON file, so a week's
worth of backlog (pls don't tell anyone) goes in one run instead of one form at a time.

It drives a real Chromium window through Playwright. Nothing is hidden: you watch it click.

## Requirements

- JDK 21+
- The first run downloads the Chromium bundle Playwright ships with, so it needs network and a
  couple of minutes.

## Build

```
./gradlew clean jar
```

That produces a single runnable jar under `build/libs/`, dependencies included. It is large
(~200 MB) because the Playwright driver bundle carries its own browser.

## Run

```
java -jar build/libs/asall-0.1.jar daily-executions.json
```

What happens:

1. A browser opens on asa.poja.io and waits for you to log in **by hand**. No credentials are read,
   stored, or asked for anywhere in this project. It simply polls until the authenticated menus show
   up, giving you 5 minutes.
2. Each entry in the file is submitted in order. The first failure stops the run, and every entry
   that did not make it is logged so you know where to pick up.
3. When it is done, the browser stays open and the process waits. Press Enter in the terminal to
   close it.

## Input file

A list of days, each with its tasks:

```json
[
  {
    "date": "2026-08-11",
    "tasks": [
      {
        "score": "0.6",
        "category": "CA-ABNP",
        "description": "asall: automated the daily execution form"
      },
      {
        "score": "0.4",
        "category": "HMMM",
        "description": "code review"
      }
    ]
  }
]
```

- `date` is ISO, `yyyy-MM-dd`.
- `score` is a share of the day, one decimal. The scores of a day must add up to exactly 1. If any
  day does not, the file is rejected before the browser even opens, since the site would turn it
  down anyway.
- `category` must match one of the mission codes in the form's dropdown.
- The form holds 5 rows, so a day with more than 5 tasks is rejected rather than silently truncated.