# CLAUDE.md — BlockUnknownCalls

Android app (Kotlin) that silently blocks calls from numbers not in the user's contacts, using Android's `CallScreeningService` API. Portfolio/showcase project.

## Stack

| Layer | Tech |
|-------|------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture (Domain → Data → Presentation) |
| DI | Koin |
| Build | Gradle (Version Catalog, `libs.versions.toml`) |
| Min SDK | 29 (Android 10, required for CallScreeningService) |
| CI | GitHub Actions |

## Architecture — Dependency Rules

```
presentation/ → domain/ ← data/
```

- `domain/` is pure Kotlin — **no `android.*` imports** (except annotations)
- `presentation/` must never import `data/` directly
- `domain/repository/` contains only interfaces; implementations live in `data/`
- DI wiring lives exclusively in `di/AppModule.kt`

## Implementation Status

### Phase 1 — MVP (complete)

| Component | File |
|-----------|------|
| `CallScreeningServiceImpl` | `data/service/` |
| `ValidateContactUseCase` | `domain/usecase/` |
| `ContactRepositoryImpl` | `data/repository/` |
| `PhoneNumberNormalizer` | `data/util/` |
| `OnboardingScreen` + ViewModel | `presentation/onboarding/` |
| `HomeScreen` + ViewModel | `presentation/home/` |
| Koin DI | `di/AppModule.kt` |
| GitHub Actions CI | `.github/workflows/` |

### Phase 2 — Pending

- Room Database (call history + analytics)
- Whitelist/Blacklist manual UI
- Block notifications
- Unit tests (80% coverage target)
- Detekt + ktlint

## Build Blockers (resolve before first build)

1. **`gradle-wrapper.jar` missing** — run `gradle wrapper` or:
   ```bash
   curl -L -o gradle/wrapper/gradle-wrapper.jar \
     https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar
   ```
2. **`local.properties` missing** — create at root:
   ```properties
   sdk.dir=/Users/<YOU>/Library/Android/sdk
   ```
3. **Launcher icons missing** — use Android Studio: `res` → New → Image Asset

## Essential Commands

```bash
chmod +x gradlew           # first time only
./gradlew assembleDebug    # build
./gradlew installDebug     # build + install on device/emulator
./gradlew lintDebug        # lint check
./gradlew clean build      # clean build
```

## Key Conventions

- **Use Cases**: named `<Verb><Noun>UseCase`, expose `operator fun invoke()`
- **Repositories**: interface in `domain/`, implementation as `<Name>RepositoryImpl` in `data/`
- **Composables**: PascalCase, stateless preferred; ViewModels injected via `viewModel()`
- **Commit format**: `type(scope): description` — e.g. `feat(domain): add ValidateContactUseCase`

## References

- `docs/CLAUDE.md` — full coding conventions and naming rules
- `docs/SPEC.md` — functional requirements and architecture decisions
- `NEXT_STEPS.md` — build checklist and Phase 2 roadmap
