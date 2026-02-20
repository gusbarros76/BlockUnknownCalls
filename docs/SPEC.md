# BlockUnknownCalls - Especificação Técnica

> **Versão**: 1.0.0  
> **Última Atualização**: 2025-02-10  
> **Autor**: Gustavo Barros Messora

## 📋 Visão Geral

App Android que bloqueia automaticamente chamadas de números que NÃO estão salvos nos contatos do usuário.

### Objetivo do Projeto
- **Funcional**: Ferramenta real para bloqueio de chamadas indesejadas
- **Showcase**: Demonstrar expertise em arquitetura Android moderna para portfolio

---

## 🎯 Requisitos Funcionais

### RF-001: Bloqueio Automático
- **Descrição**: Rejeitar chamadas de números não presentes nos contatos
- **Critério**: Validação via ContentProvider de contatos
- **Comportamento**: Chamada rejeitada antes de tocar

### RF-002: Onboarding
- **Descrição**: Guiar usuário na configuração inicial
- **Etapas**:
  1. Solicitar permissões runtime (READ_CONTACTS, READ_PHONE_STATE, READ_CALL_LOG)
  2. Orientar configuração manual como CallScreeningService
  3. Validar status antes de liberar app

### RF-003: Status Visual
- **Descrição**: Indicador claro se proteção está ativa
- **Estados**:
  - ✅ Ativo: App configurado corretamente
  - ❌ Inativo: Falta configuração ou permissão

---

## 🚫 Requisitos Não-Funcionais

### RNF-001: Performance
- Validação de contato em < 500ms
- Normalização de número em < 50ms

### RNF-002: Compatibilidade
- **Min SDK**: 29 (Android 10) - 94% dos dispositivos
- **Target SDK**: 34 (Android 14)
- **Motivo**: CallScreeningService disponível apenas API 29+

### RNF-003: Segurança
- **Fail-safe**: Em caso de erro, permitir chamada (evitar bloquear emergências)
- Não armazenar números de telefone (privacidade)

### RNF-004: Qualidade de Código
- Cobertura de testes: N/A no MVP (Fase 2)
- Lint: Zero warnings críticos
- Documentação inline em pontos críticos

---

## 🏗️ Arquitetura

### Padrão: Clean Architecture + MVVM
```
┌─────────────────────────────────────┐
│      Presentation Layer             │
│  (Compose UI + ViewModels)          │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Domain Layer                  │
│  (Use Cases + Repository Interfaces)│
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Data Layer                   │
│  (Repositories + CallScreeningService)│
└─────────────────────────────────────┘
```

### Módulos

**Single-module app** (simplicidade do MVP)
```
app/
├── presentation/
│   ├── onboarding/      # Fluxo de setup inicial
│   ├── home/            # Tela principal com status
│   └── theme/           # Material 3 theming
├── domain/
│   ├── usecase/         # Lógica de negócio isolada
│   └── repository/      # Interfaces (contratos)
├── data/
│   ├── repository/      # Implementações
│   ├── service/         # CallScreeningService
│   └── util/            # Phone number normalization
└── di/                  # Koin modules
```

---

## 📦 Stack Tecnológica

### Core
- **Linguagem**: Kotlin 1.9.20
- **Build**: Gradle 8.2 (Kotlin DSL) + Version Catalog
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 34 (Android 14)

### UI
- **Framework**: Jetpack Compose (BOM 2024.02.00)
- **Design System**: Material 3
- **Navigation**: Compose Navigation (stateful - onboarding → home)

### Architecture
- **Pattern**: Clean Architecture + MVVM
- **DI**: Koin 3.5.0
- **Async**: Coroutines + Flow
- **State Management**: StateFlow

### Android Components
- **CallScreeningService**: Core blocking functionality
- **ContentProvider**: Contact lookup via ContactsContract
- **RoleManager**: Request ROLE_CALL_SCREENING

### Quality (Fase 2)
- **Testing**: JUnit 5 + MockK + Turbine
- **Lint**: Android Lint + Detekt
- **CI/CD**: GitHub Actions

---

## 🗂️ Estrutura de Arquivos

### Diretórios Principais
```
BlockUnknownCalls/
├── .github/
│   └── workflows/
│       └── ci.yml                    # Build + Lint automation
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gusbarros/blockcalls/
│   │   │   │   ├── BlockCallsApplication.kt
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── onboarding/
│   │   │   │   │   │   ├── OnboardingScreen.kt
│   │   │   │   │   │   └── OnboardingViewModel.kt
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── usecase/
│   │   │   │   │   │   └── ValidateContactUseCase.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── ContactRepository.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   └── ContactRepositoryImpl.kt
│   │   │   │   │   ├── service/
│   │   │   │   │   │   └── CallScreeningServiceImpl.kt
│   │   │   │   │   └── util/
│   │   │   │   │       └── PhoneNumberNormalizer.kt
│   │   │   │   └── di/
│   │   │   │       └── AppModule.kt
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/
│   │   │       ├── values/
│   │   │       │   ├── strings.xml
│   │   │       │   ├── colors.xml
│   │   │       │   └── themes.xml
│   │   │       └── mipmap-*/         # App icons
│   │   └── test/
│   │       └── java/                 # Unit tests (Fase 2)
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml            # Centralized dependency versions
├── docs/
│   ├── claude.md                     # Claude Code working instructions
│   ├── SPEC.md                       # This file
│   └── ARCHITECTURE.md               # ADRs
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
├── README.md
└── LICENSE
```

---

## 🔑 Componentes Críticos

### 1. CallScreeningServiceImpl

**Responsabilidade**: Interceptar chamadas e decidir bloquear/permitir

**Fluxo**:
```kotlin
onScreenCall(callDetails) {
    1. Extrair número do callDetails.handle
    2. Validar se não é nulo/vazio → permitir se for
    3. Chamar ValidateContactUseCase(número)
    4. Se contato → permitir
    5. Se desconhecido → bloquear
    6. Em caso de exceção → permitir (fail-safe)
}
```

**Configuração**:
- Registrado no AndroidManifest com `BIND_SCREENING_SERVICE`
- Utiliza CoroutineScope com SupervisorJob (não crashar app em erro)

---

### 2. ValidateContactUseCase

**Responsabilidade**: Lógica de negócio de validação

**Processo**:
```kotlin
invoke(phoneNumber: String): Boolean {
    1. Normalizar número (PhoneNumberNormalizer)
    2. Consultar ContactRepository
    3. Retornar booleano
}
```

---

### 3. ContactRepositoryImpl

**Responsabilidade**: Acesso ao ContentProvider de contatos

**Implementação**:
```kotlin
isNumberInContacts(phoneNumber: String): Boolean {
    1. Criar URI: ContactsContract.PhoneLookup.CONTENT_FILTER_URI
    2. Query via ContentResolver
    3. Retornar cursor.count > 0
    4. Executar em Dispatchers.IO
}
```

---

### 4. PhoneNumberNormalizer

**Responsabilidade**: Normalizar números brasileiros

**Regras**:
- Remover caracteres especiais: `()-.` e espaços
- Remover código do país: `+55` ou `55` no início
- Manter apenas dígitos

**Exemplos**:
```
+55 11 98765-4321  → 11987654321
(11) 98765-4321    → 11987654321
55 11 98765-4321   → 11987654321
11987654321        → 11987654321
```

---

### 5. OnboardingScreen

**Responsabilidade**: Guiar setup inicial

**Estados**:
1. **Permissões Pendentes**: Botão "Conceder Permissões"
2. **Role Pendente**: Botão "Configurar Bloqueio" (abre Settings)
3. **Completo**: Navega automaticamente para HomeScreen

**Validações**:
- READ_CONTACTS
- READ_PHONE_STATE
- READ_CALL_LOG
- RoleManager.ROLE_CALL_SCREENING

---

### 6. HomeScreen

**Responsabilidade**: Mostrar status da proteção

**UI**:
- **Card de Status**: Verde (ativo) / Vermelho (inativo)
- **Card de Informações**: Como funciona o bloqueio
- Sem toggle (configuração via Settings do Android)

---

## 🔐 Permissões e Segurança

### Permissões Necessárias
```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission android:name="android.permission.READ_PHONE_NUMBERS" />
```

### Role Necessário
- `RoleManager.ROLE_CALL_SCREENING` (configuração manual obrigatória)

### Considerações de Segurança
- **Fail-safe**: Sempre permitir chamada em caso de erro
- **Privacidade**: Não armazenar/log números de telefone
- **Transparência**: UI clara sobre o que o app faz

---

## 📊 Dados e Estado

### Estado do App

**Persistência**: NÃO há (MVP)
- Fase 2: Room para cache de decisões e analytics

**Estado Runtime**:
- `OnboardingState`: Permissões e role status
- `HomeState`: isActive (boolean)

**Fonte de Verdade**:
- RoleManager (para status de ativação)
- ContentProvider (para contatos)

---

## 🚀 CI/CD

### GitHub Actions

**Workflow**: `.github/workflows/ci.yml`

**Triggers**:
- Push em `main` ou `develop`
- Pull Requests para `main`

**Jobs**:
1. **Lint**: `./gradlew lintDebug`
2. **Build**: `./gradlew assembleDebug`
3. **Upload Artifact**: APK debug disponível por 7 dias

**Requisitos**:
- JDK 17 (Temurin)
- Ubuntu latest
- Gradle cache habilitado

---

## 📝 Convenções de Código

### Naming

**Packages**:
- `presentation` (não `ui`)
- `domain` (não `business`)
- `data` (não `repository`)

**Classes**:
- ViewModels: `*ViewModel` (ex: `OnboardingViewModel`)
- Screens: `*Screen` (ex: `HomeScreen`)
- Use Cases: `*UseCase` (ex: `ValidateContactUseCase`)
- Repositories: `*Repository` / `*RepositoryImpl`

**Composables**:
- PascalCase (ex: `OnboardingScreen`)
- Stateless quando possível
- State hoisting para lógica complexa

---

## 🧪 Testes (Fase 2)

### Cobertura Planejada

**Unit Tests**:
- `PhoneNumberNormalizer` (100% coverage)
- `ValidateContactUseCase` (mocks do repository)
- ViewModels (StateFlow testing com Turbine)

**Instrumented Tests**:
- `ContactRepositoryImpl` (ContentProvider real)
- Fluxo de permissões

**Não Testável**:
- `CallScreeningServiceImpl` (requer sistema Android real)

---

## 🗺️ Roadmap

### Fase 1: MVP (atual)
- [x] Definir especificação
- [ ] Implementar core functionality
- [ ] UI básica funcional
- [ ] CI/CD
- [ ] README showcase

### Fase 2: Robustez
- [ ] Room Database (cache de decisões)
- [ ] Whitelist/Blacklist manual
- [ ] Notificações de chamadas bloqueadas
- [ ] Analytics básicos (quantidade de bloqueios)

### Fase 3: Polish
- [ ] Testes unitários (80% coverage)
- [ ] Testes instrumentados
- [ ] Detekt + ktlint CI
- [ ] Release no GitHub com changelog

---

## 🐛 Riscos Conhecidos

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Usuário não ativa Role | Alta | Alto | UX claro no onboarding |
| Normalização falha números internacionais | Média | Médio | Fase 2: libphonenumber |
| Performance em query de contatos | Baixa | Médio | Dispatchers.IO + timeout |
| ContentProvider retorna null | Baixa | Alto | Elvis operator com fallback |

---

## 📚 Referências

### Documentação Android
- [CallScreeningService](https://developer.android.com/reference/android/telecom/CallScreeningService)
- [RoleManager](https://developer.android.com/reference/android/app/role/RoleManager)
- [ContactsContract](https://developer.android.com/reference/android/provider/ContactsContract)

### Código de Referência
- [Now in Android](https://github.com/android/nowinandroid) - Google's architecture sample
- [Jetpack Compose Samples](https://github.com/android/compose-samples)

---

## 📞 Contato

**Desenvolvedor**: Gustavo Barros Messora  
**GitHub**: [@gusbarros76](https://github.com/gusbarros76)  
**LinkedIn**: [Gustavo Barros Messora](https://www.linkedin.com/in/gustavobarrosmessora)

---

**Versão**: 1.0.0  
**Status**: Em Desenvolvimento  
**Última Revisão**: 2025-02-10