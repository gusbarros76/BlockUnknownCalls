# Design: README (EN) + GitHub Repo Setup

**Date:** 2026-02-20
**Status:** Approved

## Goal

Publish BlockUnknownCalls as a public GitHub portfolio repo with an English README that highlights the modern Android stack.

## README Structure

1. **Header** — name, tagline, badges (CI, Kotlin, Min SDK, License)
2. **Modern Stack** — prominent table with versions; Compose, Koin, Coroutines, Gradle Version Catalog, GH Actions
3. **Architecture** — ASCII diagram + dependency rule in one line
4. **How It Works** — 3 code snippets: CallScreeningService, BR phone normalization, ContentProvider query
5. **Getting Started** — prerequisites + 3 commands
6. **Roadmap** — Phase 1 ✅ / Phase 2 pending
7. **Author** — GitHub + LinkedIn

## Repo Setup

- Name: `BlockUnknownCalls`
- Visibility: Public
- Branch: `main`
- .gitignore: Android standard
- Single clean initial commit

## Decisions

- Rewrite existing PT README in English; more professional for portfolio
- Remove "Contributing" and "Thanks" sections (noise for portfolio)
- Keep code snippets to show technical depth without verbosity
