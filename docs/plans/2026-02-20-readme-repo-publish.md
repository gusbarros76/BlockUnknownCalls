# README (EN) + GitHub Repo Publish — Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Rewrite README in English (hybrid portfolio style, modern stack focus), publish as a public GitHub repo.

**Architecture:** No code changes — file edits + git operations only.

**Tech Stack:** Markdown, git, GitHub CLI (`gh`)

---

### Task 1: Rewrite README.md in English

**Files:**
- Overwrite: `README.md`

**Step 1: Write the new README.md**

Replace the entire content of `README.md` with:

```markdown
# 🛡️ BlockUnknownCalls

> Silently reject calls from numbers not in your contacts — no interruptions, no spam.

[![Android CI](https://github.com/gusbarros76/BlockUnknownCalls/workflows/Android%20CI/badge.svg)](https://github.com/gusbarros76/BlockUnknownCalls/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-API%2029-brightgreen.svg)](https://developer.android.com/about/versions/10)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

An Android app that uses the `CallScreeningService` API to silently block incoming calls from numbers not saved in your contacts. Built as a portfolio project to demonstrate modern Android development practices.

---

## Modern Stack

|  | Technology | Version |
|--|-----------|---------|
| **Language** | Kotlin | 1.9.20 |
| **UI** | Jetpack Compose + Material 3 | BOM 2023.10 |
| **Architecture** | Clean Architecture + MVVM | — |
| **DI** | Koin | 3.5.0 |
| **Async** | Coroutines + Flow | 1.7.3 |
| **Build** | Gradle Kotlin DSL + Version Catalog | 8.2 |
| **CI/CD** | GitHub Actions | — |

---

## Architecture

```
Presentation  →  Domain  ←  Data
(Compose UI)    (UseCases)  (Repositories)
                 pure Kotlin, no Android SDK
```

Dependency rule: `presentation` and `data` depend on `domain` — never the reverse.

---

## How It Works

**1. Call interception via `CallScreeningService`**

```kotlin
override fun onScreenCall(callDetails: Call.Details) {
    val phoneNumber = callDetails.handle?.schemeSpecificPart

    if (validateContactUseCase(phoneNumber)) {
        respondToCall(callDetails, allowCall())
    } else {
        respondToCall(callDetails, rejectCall()) // unknown → silent reject
    }
}
```

**2. Brazilian phone number normalization**

```kotlin
"+55 11 98765-4321"  →  "11987654321"
"(11) 98765-4321"    →  "11987654321"
```

**3. Contact lookup via `ContentProvider`**

```kotlin
val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI
    .buildUpon().appendPath(normalizedNumber).build()

contentResolver.query(uri, ...).use { cursor ->
    cursor.count > 0  // true = known contact → allow
}
```

---

## Getting Started

**Requirements:** Android Studio Hedgehog+, JDK 17, Android 10+ (API 29+)

```bash
git clone https://github.com/gusbarros76/BlockUnknownCalls.git
cd BlockUnknownCalls
./gradlew assembleDebug
./gradlew installDebug
```

On first launch: grant Contacts and Phone permissions, then set the app as your **Call Screening Service** in Android settings.

---

## Roadmap

**Phase 1 — MVP** ✅
- [x] `CallScreeningService` implementation
- [x] Clean Architecture (Domain → Data → Presentation)
- [x] Jetpack Compose onboarding + home screen
- [x] GitHub Actions CI/CD

**Phase 2 — In Progress**
- [ ] Room Database (call history + analytics)
- [ ] Manual whitelist / blacklist
- [ ] Block notifications
- [ ] Unit tests (80% coverage target)
- [ ] Detekt + ktlint

---

## Author

**Gustavo Barros**

- GitHub: [@gusbarros76](https://github.com/gusbarros76)
- LinkedIn: [Gustavo Barros Messora](https://www.linkedin.com/in/gustavobarrosmessora)

---

<div align="center">
<sub>Built with Kotlin · Jetpack Compose · Clean Architecture</sub>
</div>
```

**Step 2: Verify the file looks right**

```bash
wc -l README.md
# Expected: ~90 lines
```

**Step 3: No commit yet — proceed to Task 2**

---

### Task 2: Initialize git repo and create initial commit

**Files:** All project files (respecting `.gitignore`)

**Step 1: Initialize git with main branch**

```bash
git init -b main
```
Expected output: `Initialized empty Git repository in .../BlockUnknownCalls/.git/`

**Step 2: Stage all files**

```bash
git add .
```

**Step 3: Verify what will be committed (spot-check)**

```bash
git status
```
Expected: staged files include `README.md`, `CLAUDE.md`, `app/`, `docs/`, `gradle/`, `.github/`
NOT included: `local.properties`, `build.log`, `.DS_Store`, `build/`

**Step 4: Create initial commit**

```bash
git commit -m "$(cat <<'EOF'
feat: initial MVP — BlockUnknownCalls

- Clean Architecture (Domain → Data → Presentation)
- CallScreeningService implementation
- ValidateContactUseCase + ContactRepositoryImpl
- PhoneNumberNormalizer (Brazilian format)
- Jetpack Compose UI (Onboarding + Home)
- Koin DI
- GitHub Actions CI/CD
- Portfolio README (EN)

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"
```

---

### Task 3: Create public GitHub repo and push

**Step 1: Create public repo on GitHub and push**

```bash
gh repo create BlockUnknownCalls \
  --public \
  --source=. \
  --remote=origin \
  --push \
  --description="Android app that silently blocks calls from unknown numbers. Built with Kotlin, Jetpack Compose, Clean Architecture."
```

Expected: outputs the new repo URL (e.g. `https://github.com/gusbarros76/BlockUnknownCalls`)

**Step 2: Verify repo is live**

```bash
gh repo view gusbarros76/BlockUnknownCalls
```

Expected: shows repo name, description, public visibility, and the README content.

**Step 3: Open in browser to visually confirm**

```bash
gh repo view gusbarros76/BlockUnknownCalls --web
```
