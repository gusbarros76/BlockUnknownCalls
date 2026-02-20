# Design: Fase 2 MVP — Room + Counter + Tests + Detekt

**Date:** 2026-02-20
**Status:** Approved

## Scope

Three parallel workstreams, all independent:

| Workstream | MVP Scope |
|------------|-----------|
| **Room** | 1 table, timestamp only (no phone number — privacy) |
| **Counter** | HomeScreen shows "X calls blocked" via StateFlow |
| **Tests** | PhoneNumberNormalizer + ValidateContactUseCase + HomeViewModel |
| **Quality** | Detekt static analysis (ktlint deferred) |

## Room Architecture

Clean Architecture maintained — Room never leaks into domain.

### New files

```
domain/model/BlockedCall.kt                     ← pure data class
domain/repository/CallHistoryRepository.kt      ← interface
domain/usecase/RecordBlockedCallUseCase.kt      ← called on block

data/local/BlockedCallEntity.kt                 ← @Entity
data/local/BlockedCallDao.kt                    ← @Dao (count query)
data/local/BlockCallsDatabase.kt                ← @Database
data/repository/CallHistoryRepositoryImpl.kt

di/AppModule.kt                                 ← add Room + new usecase
```

### Modified files

- `CallScreeningServiceImpl` — calls `RecordBlockedCallUseCase` on block
- `HomeViewModel` — adds `blockedCount: StateFlow<Int>`
- `HomeScreen` — displays blocked count card

### Privacy

`BlockedCallEntity` stores only `id: Long` + `blockedAt: Long` — no phone number (SPEC RNF-003).

## Test Strategy

Framework: JUnit 4 + MockK + Turbine + kotlinx-coroutines-test

| Test class | Approach |
|-----------|----------|
| `PhoneNumberNormalizerTest` | Pure — parametrized input/output table |
| `ValidateContactUseCaseTest` | MockK ContactRepository — allow/block/exception cases |
| `RecordBlockedCallUseCaseTest` | MockK CallHistoryRepository |
| `HomeViewModelTest` | Turbine for StateFlow collection |

No instrumented tests in MVP — deferred to Phase 3.

## Code Quality

- Detekt plugin on root `build.gradle.kts`
- `detekt.yml` at root with relaxed rules (no aggressive false positives)
- CI: add `./gradlew detekt` step before build

## Dependencies to Add (libs.versions.toml)

- `room = "2.6.1"`
- `junit = "4.13.2"`
- `mockk = "1.13.8"`
- `turbine = "1.0.0"`
- `coroutines-test` (use existing coroutines version `1.7.3`)
- `detekt = "1.23.4"`
