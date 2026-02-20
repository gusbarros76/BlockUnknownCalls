# Phase 2 MVP Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add Room call counter, unit tests for domain layer, and Detekt static analysis.

**Architecture:** Clean Architecture maintained throughout — Room lives only in `data/local/`, exposed to domain via `CallHistoryRepository` interface. `CallScreeningServiceImpl` records each blocked call via `RecordBlockedCallUseCase`. `HomeViewModel` observes the count as a `Flow<Int>` and exposes it via `HomeState`.

**Tech Stack:** Room 2.6.1, JUnit 4, MockK 1.13.8, Turbine 1.0.0, kotlinx-coroutines-test, Detekt 1.23.4, kapt

---

### Task 1: Add all dependencies (libs.versions.toml + build files)

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `build.gradle.kts` (root)

**Step 1: Update `gradle/libs.versions.toml`**

Add to `[versions]`:
```toml
room = "2.6.1"
junit = "4.13.2"
mockk = "1.13.8"
turbine = "1.0.0"
detekt = "1.23.4"
```

Add to `[libraries]`:
```toml
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
junit = { module = "junit:junit", version.ref = "junit" }
mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
turbine = { module = "app.cash.turbine:turbine", version.ref = "turbine" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "coroutines" }
```

Add to `[plugins]`:
```toml
detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }
```

**Step 2: Update `app/build.gradle.kts`**

Add `id("kotlin-kapt")` to the plugins block:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}
```

Add Room + test dependencies to the `dependencies` block:
```kotlin
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
```

**Step 3: Update root `build.gradle.kts`**

Replace the file content with:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt)
}

detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
    source.setFrom(
        "app/src/main/java",
        "app/src/test/java"
    )
}
```

**Step 4: Verify it compiles**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL (no new code yet, just config)

**Step 5: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts build.gradle.kts
git commit -m "build: add Room, test, and Detekt dependencies"
```

---

### Task 2: Detekt setup

**Files:**
- Create: `detekt.yml` (root)

**Step 1: Create `detekt.yml`**

```yaml
build:
  maxIssues: 0

config:
  validation: true

complexity:
  LongMethod:
    threshold: 60
  LongParameterList:
    functionThreshold: 8
    constructorThreshold: 8

style:
  MagicNumber:
    active: false
  WildcardImport:
    active: false
  MaxLineLength:
    maxLineLength: 140

naming:
  FunctionNaming:
    functionPattern: '[a-z][a-zA-Z0-9]*|`[^`]+`'
```

**Step 2: Run Detekt**

```bash
./gradlew detekt
```
Expected: BUILD SUCCESSFUL or list of violations to fix. If violations appear, fix them before proceeding.

**Step 3: Commit**

```bash
git add detekt.yml
git commit -m "chore: add Detekt configuration"
```

---

### Task 3: PhoneNumberNormalizerTest

**Files:**
- Create: `app/src/test/java/com/gusbarros/blockcalls/data/util/PhoneNumberNormalizerTest.kt`

**Step 1: Create test directory structure**

```bash
mkdir -p app/src/test/java/com/gusbarros/blockcalls/data/util
mkdir -p app/src/test/java/com/gusbarros/blockcalls/domain/usecase
```

**Step 2: Write the test**

```kotlin
package com.gusbarros.blockcalls.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberNormalizerTest {

    @Test
    fun `normalize removes +55 country code and formatting`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("+55 11 98765-4321"))
    }

    @Test
    fun `normalize removes parentheses and hyphens`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("(11) 98765-4321"))
    }

    @Test
    fun `normalize leaves already clean number unchanged`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("11987654321"))
    }

    @Test
    fun `normalize removes 55 prefix from long number`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("5511987654321"))
    }

    @Test
    fun `normalize handles empty string`() {
        assertEquals("", PhoneNumberNormalizer.normalize(""))
    }

    @Test
    fun `normalize handles number without country code but with spaces`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("11 98765-4321"))
    }
}
```

**Step 3: Run the tests**

```bash
./gradlew test --tests "com.gusbarros.blockcalls.data.util.PhoneNumberNormalizerTest"
```
Expected: 6 tests PASS

**Step 4: Commit**

```bash
git add app/src/test/
git commit -m "test: add PhoneNumberNormalizerTest (100% coverage)"
```

---

### Task 4: ValidateContactUseCaseTest

**Files:**
- Create: `app/src/test/java/com/gusbarros/blockcalls/domain/usecase/ValidateContactUseCaseTest.kt`

**Step 1: Write the test**

```kotlin
package com.gusbarros.blockcalls.domain.usecase

import com.gusbarros.blockcalls.domain.repository.ContactRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateContactUseCaseTest {

    private lateinit var contactRepository: ContactRepository
    private lateinit var useCase: ValidateContactUseCase

    @Before
    fun setUp() {
        contactRepository = mockk()
        useCase = ValidateContactUseCase(contactRepository)
    }

    @Test
    fun `invoke returns true when number is in contacts`() = runTest {
        coEvery { contactRepository.isNumberInContacts(any()) } returns true

        val result = useCase("11987654321")

        assertTrue(result)
    }

    @Test
    fun `invoke returns false when number is not in contacts`() = runTest {
        coEvery { contactRepository.isNumberInContacts(any()) } returns false

        val result = useCase("99999999999")

        assertFalse(result)
    }

    @Test
    fun `invoke passes phone number through to repository`() = runTest {
        val phoneNumber = "11987654321"
        coEvery { contactRepository.isNumberInContacts(phoneNumber) } returns true

        useCase(phoneNumber)

        io.mockk.coVerify(exactly = 1) { contactRepository.isNumberInContacts(phoneNumber) }
    }
}
```

**Step 2: Run the tests**

```bash
./gradlew test --tests "com.gusbarros.blockcalls.domain.usecase.ValidateContactUseCaseTest"
```
Expected: 3 tests PASS

**Step 3: Commit**

```bash
git add app/src/test/java/com/gusbarros/blockcalls/domain/usecase/ValidateContactUseCaseTest.kt
git commit -m "test: add ValidateContactUseCaseTest"
```

---

### Task 5: Domain layer — CallHistory model + interface + use case

**Files:**
- Create: `app/src/main/java/com/gusbarros/blockcalls/domain/model/BlockedCall.kt`
- Create: `app/src/main/java/com/gusbarros/blockcalls/domain/repository/CallHistoryRepository.kt`
- Create: `app/src/main/java/com/gusbarros/blockcalls/domain/usecase/RecordBlockedCallUseCase.kt`

**Step 1: Create domain model**

`app/src/main/java/com/gusbarros/blockcalls/domain/model/BlockedCall.kt`:
```kotlin
package com.gusbarros.blockcalls.domain.model

data class BlockedCall(
    val id: Long = 0,
    val blockedAt: Long = System.currentTimeMillis()
)
```

**Step 2: Create repository interface**

`app/src/main/java/com/gusbarros/blockcalls/domain/repository/CallHistoryRepository.kt`:
```kotlin
package com.gusbarros.blockcalls.domain.repository

import kotlinx.coroutines.flow.Flow

interface CallHistoryRepository {
    suspend fun recordBlockedCall()
    fun getBlockedCount(): Flow<Int>
}
```

**Step 3: Create use case**

`app/src/main/java/com/gusbarros/blockcalls/domain/usecase/RecordBlockedCallUseCase.kt`:
```kotlin
package com.gusbarros.blockcalls.domain.usecase

import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository

class RecordBlockedCallUseCase(
    private val callHistoryRepository: CallHistoryRepository
) {
    suspend operator fun invoke() {
        callHistoryRepository.recordBlockedCall()
    }
}
```

**Step 4: Verify it compiles**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

---

### Task 6: RecordBlockedCallUseCaseTest (TDD)

**Files:**
- Create: `app/src/test/java/com/gusbarros/blockcalls/domain/usecase/RecordBlockedCallUseCaseTest.kt`

**Step 1: Write the failing test (use case doesn't exist yet in test context)**

```kotlin
package com.gusbarros.blockcalls.domain.usecase

import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RecordBlockedCallUseCaseTest {

    private lateinit var callHistoryRepository: CallHistoryRepository
    private lateinit var useCase: RecordBlockedCallUseCase

    @Before
    fun setUp() {
        callHistoryRepository = mockk()
        useCase = RecordBlockedCallUseCase(callHistoryRepository)
    }

    @Test
    fun `invoke calls recordBlockedCall on repository`() = runTest {
        coJustRun { callHistoryRepository.recordBlockedCall() }

        useCase()

        coVerify(exactly = 1) { callHistoryRepository.recordBlockedCall() }
    }
}
```

**Step 2: Run the test**

```bash
./gradlew test --tests "com.gusbarros.blockcalls.domain.usecase.RecordBlockedCallUseCaseTest"
```
Expected: PASS

**Step 3: Commit domain layer + tests**

```bash
git add app/src/main/java/com/gusbarros/blockcalls/domain/ \
        app/src/test/java/com/gusbarros/blockcalls/domain/usecase/RecordBlockedCallUseCaseTest.kt
git commit -m "feat(domain): add CallHistory model, repository interface, and RecordBlockedCallUseCase"
```

---

### Task 7: Room data layer

**Files:**
- Create: `app/src/main/java/com/gusbarros/blockcalls/data/local/BlockedCallEntity.kt`
- Create: `app/src/main/java/com/gusbarros/blockcalls/data/local/BlockedCallDao.kt`
- Create: `app/src/main/java/com/gusbarros/blockcalls/data/local/BlockCallsDatabase.kt`
- Create: `app/src/main/java/com/gusbarros/blockcalls/data/repository/CallHistoryRepositoryImpl.kt`

**Step 1: Create Room entity**

```kotlin
package com.gusbarros.blockcalls.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_calls")
data class BlockedCallEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val blockedAt: Long = System.currentTimeMillis()
)
```

**Step 2: Create DAO**

```kotlin
package com.gusbarros.blockcalls.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallDao {
    @Insert
    suspend fun insert(call: BlockedCallEntity)

    @Query("SELECT COUNT(*) FROM blocked_calls")
    fun getCount(): Flow<Int>
}
```

**Step 3: Create Database**

```kotlin
package com.gusbarros.blockcalls.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BlockedCallEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BlockCallsDatabase : RoomDatabase() {
    abstract fun blockedCallDao(): BlockedCallDao
}
```

**Step 4: Create repository implementation**

```kotlin
package com.gusbarros.blockcalls.data.repository

import com.gusbarros.blockcalls.data.local.BlockedCallDao
import com.gusbarros.blockcalls.data.local.BlockedCallEntity
import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.Flow

class CallHistoryRepositoryImpl(
    private val dao: BlockedCallDao
) : CallHistoryRepository {

    override suspend fun recordBlockedCall() {
        dao.insert(BlockedCallEntity())
    }

    override fun getBlockedCount(): Flow<Int> = dao.getCount()
}
```

**Step 5: Verify it compiles**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL (kapt generates Room code)

**Step 6: Commit**

```bash
git add app/src/main/java/com/gusbarros/blockcalls/data/
git commit -m "feat(data): add Room database, DAO, and CallHistoryRepositoryImpl"
```

---

### Task 8: Update DI — AppModule

**Files:**
- Modify: `app/src/main/java/com/gusbarros/blockcalls/di/AppModule.kt`

**Step 1: Replace the entire AppModule content**

```kotlin
package com.gusbarros.blockcalls.di

import androidx.room.Room
import com.gusbarros.blockcalls.data.local.BlockCallsDatabase
import com.gusbarros.blockcalls.data.repository.CallHistoryRepositoryImpl
import com.gusbarros.blockcalls.data.repository.ContactRepositoryImpl
import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import com.gusbarros.blockcalls.domain.repository.ContactRepository
import com.gusbarros.blockcalls.domain.usecase.RecordBlockedCallUseCase
import com.gusbarros.blockcalls.domain.usecase.ValidateContactUseCase
import com.gusbarros.blockcalls.presentation.home.HomeViewModel
import com.gusbarros.blockcalls.presentation.onboarding.OnboardingViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Data Layer - Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            BlockCallsDatabase::class.java,
            "block_calls_db"
        ).build()
    }

    single { get<BlockCallsDatabase>().blockedCallDao() }

    // Data Layer - Repository Implementations
    single<ContactRepository> {
        ContactRepositoryImpl(contentResolver = androidContext().contentResolver)
    }

    single<CallHistoryRepository> {
        CallHistoryRepositoryImpl(dao = get())
    }

    // Domain Layer - Use Cases
    factory { ValidateContactUseCase(contactRepository = get()) }
    factory { RecordBlockedCallUseCase(callHistoryRepository = get()) }

    // Presentation Layer - ViewModels
    viewModel { OnboardingViewModel(application = androidApplication()) }
    viewModel { HomeViewModel(application = androidApplication(), callHistoryRepository = get()) }
}
```

**Step 2: Verify it compiles**

```bash
./gradlew assembleDebug
```
Expected: compilation error because HomeViewModel doesn't accept `callHistoryRepository` yet — that's fixed in Task 10. For now this is expected.

Actually, skip verification here and proceed to Task 9 first.

---

### Task 9: Update CallScreeningServiceImpl — record blocked calls

**Files:**
- Modify: `app/src/main/java/com/gusbarros/blockcalls/data/service/CallScreeningServiceImpl.kt`

**Step 1: Add `recordBlockedCallUseCase` injection and call it when blocking**

Add the inject line after the existing `validateContactUseCase`:
```kotlin
private val validateContactUseCase: ValidateContactUseCase by inject()
private val recordBlockedCallUseCase: RecordBlockedCallUseCase by inject()
```

In the `else` branch (unknown number — blocking call), add the recording call before the response is built. The relevant section inside `serviceScope.launch { try { ... } }` becomes:

```kotlin
val isContact = validateContactUseCase(phoneNumber)

val response = if (isContact) {
    Log.d(TAG, "Contact found - allowing call")
    CallResponse.Builder()
        .setDisallowCall(false)
        .setRejectCall(false)
        .build()
} else {
    Log.d(TAG, "Unknown number - blocking call")
    recordBlockedCallUseCase()
    CallResponse.Builder()
        .setDisallowCall(true)
        .setRejectCall(true)
        .setSkipCallLog(false)
        .setSkipNotification(false)
        .build()
}
```

Also add the import at the top:
```kotlin
import com.gusbarros.blockcalls.domain.usecase.RecordBlockedCallUseCase
```

---

### Task 10: Update HomeViewModel and HomeState

**Files:**
- Modify: `app/src/main/java/com/gusbarros/blockcalls/presentation/home/HomeViewModel.kt`

**Step 1: Replace the entire file content**

```kotlin
package com.gusbarros.blockcalls.presentation.home

import android.Manifest
import android.app.Application
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val isActive: Boolean = false,
    val blockedCount: Int = 0
)

class HomeViewModel(
    application: Application,
    private val callHistoryRepository: CallHistoryRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        checkStatus()
        observeBlockedCount()
    }

    fun checkStatus() {
        viewModelScope.launch {
            val context = getApplication<Application>()

            val hasContacts = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val hasPhoneState = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            val hasCallLog = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED

            val hasRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = context.getSystemService(RoleManager::class.java)
                roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) ?: false
            } else {
                false
            }

            _state.value = _state.value.copy(
                isActive = hasContacts && hasPhoneState && hasCallLog && hasRole
            )
        }
    }

    private fun observeBlockedCount() {
        viewModelScope.launch {
            callHistoryRepository.getBlockedCount().collect { count ->
                _state.value = _state.value.copy(blockedCount = count)
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
```

**Step 2: Verify it compiles**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 3: Commit tasks 8–10 together**

```bash
git add app/src/main/java/com/gusbarros/blockcalls/
git commit -m "feat: wire Room into service and HomeViewModel via DI

- AppModule: add Room DB, CallHistoryRepository, RecordBlockedCallUseCase
- CallScreeningServiceImpl: record blocked call on rejection
- HomeViewModel: observe blockedCount via Flow"
```

---

### Task 11: Update HomeScreen — add blocked count card

**Files:**
- Modify: `app/src/main/java/com/gusbarros/blockcalls/presentation/home/HomeScreen.kt`

**Step 1: Add the counter card after the status card**

Find the closing `}` of the status `Card` block (around line 80) and add a new card after it, before the "Como Funciona" card:

```kotlin
// Card de Contador
Card(modifier = Modifier.fillMaxWidth()) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Calls Blocked",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = state.blockedCount.toString(),
            style = MaterialTheme.typography.displaySmall
        )
    }
}
```

**Step 2: Verify it compiles**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add app/src/main/java/com/gusbarros/blockcalls/presentation/home/HomeScreen.kt
git commit -m "feat(ui): add blocked calls counter card to HomeScreen"
```

---

### Task 12: Update CI — add Detekt and test steps

**Files:**
- Modify: `.github/workflows/ci.yml`

**Step 1: Read the current ci.yml first, then add Detekt and unit test jobs**

Add after the existing `lint` job and before the `build` job:

```yaml
  detekt:
    name: Detekt
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v3
      - name: Run Detekt
        run: ./gradlew detekt

  unit-tests:
    name: Unit Tests
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v3
      - name: Run unit tests
        run: ./gradlew test
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: app/build/reports/tests/
          retention-days: 7
```

**Step 2: Run tests locally to confirm they pass**

```bash
./gradlew test
```
Expected: All tests PASS

**Step 3: Run Detekt locally**

```bash
./gradlew detekt
```
Expected: BUILD SUCCESSFUL (or fix any violations)

**Step 4: Commit**

```bash
git add .github/workflows/ci.yml
git commit -m "ci: add Detekt and unit test jobs to GitHub Actions"
```

---

### Task 13: Update README roadmap + push

**Files:**
- Modify: `README.md`

**Step 1: Update the Phase 2 checklist in README**

In the `## Roadmap` section, update **Phase 2** to reflect what's now done:

```markdown
**Phase 2 — In Progress**
- [x] Room Database (call history)
- [x] Blocked calls counter (HomeScreen)
- [x] Unit tests (PhoneNumberNormalizer, ValidateContactUseCase, RecordBlockedCallUseCase)
- [x] Detekt static analysis
- [ ] Block notifications
- [ ] Manual whitelist / blacklist
```

**Step 2: Commit and push everything**

```bash
git add README.md
git commit -m "docs: update roadmap — Phase 2 MVP complete"
git push origin main
```

**Step 3: Verify CI passes on GitHub**

```bash
gh run list --limit 5
```
Expected: the latest run shows all jobs green (lint, detekt, unit-tests, build)
