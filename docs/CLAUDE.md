# Instruções para Claude Code - BlockUnknownCalls

> Este arquivo define COMO o Claude Code deve trabalhar neste projeto.  
> Para especificações TÉCNICAS, veja `docs/SPEC.md`

---

## 🎯 Filosofia do Projeto

Este é um **projeto showcase** para portfolio profissional. Qualidade > Velocidade.

### Princípios
1. **Código limpo**: Preferir clareza a cleverness
2. **Documentação inline**: Explicar "por quês" não óbvios
3. **Fail-safe**: Em caso de dúvida, priorizar segurança do usuário
4. **Android-idiomático**: Seguir padrões oficiais do Google

---

## 📋 Ao Iniciar Qualquer Tarefa

**SEMPRE leia primeiro**:
1. `docs/SPEC.md` - Entender o QUE construir
2. `docs/ARCHITECTURE.md` - Entender as decisões tomadas
3. Este arquivo - Entender COMO trabalhar

**Antes de codificar**:
- [ ] Entendi o requisito?
- [ ] Sei onde encaixa na arquitetura?
- [ ] Há alguma convenção específica?

---

## 🏗️ Estrutura de Arquivos

### Onde Colocar Cada Coisa
```
presentation/     → Tudo relacionado a UI (Compose + ViewModels)
domain/          → Lógica de negócio PURA (sem Android SDK)
data/            → Implementações concretas (Android SDK permitido)
di/              → Módulos Koin (apenas wiring)
```

### Regra de Dependências
```
presentation → domain → data
     ↓           ↓
  ViewModel   UseCase
     ↓           ↓
   Screen   Repository Interface → Repository Impl
```

**Proibido**:
- `domain` importar `android.*` (exceto annotations)
- `presentation` importar `data` diretamente
- Classes concretas em `domain/repository` (apenas interfaces)

---

## 🎨 Convenções de Código Kotlin

### Naming

**Classes**:
```kotlin
// ✅ BOM
class ValidateContactUseCase
class ContactRepositoryImpl
class OnboardingViewModel

// ❌ RUIM
class ContactValidator
class ContactRepo
class OnboardingVM
```

**Functions**:
```kotlin
// ✅ BOM - Use Cases são operadores
operator fun invoke(phoneNumber: String): Boolean

// ✅ BOM - Repository methods descritivos
suspend fun isNumberInContacts(phoneNumber: String): Boolean

// ❌ RUIM - Verboso demais
suspend fun checkIfPhoneNumberExistsInContactsList()
```

**Composables**:
```kotlin
// ✅ BOM - PascalCase, stateless preferred
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
)

// ❌ RUIM - camelCase
@Composable
fun onboardingScreen()
```

### Organização de Arquivo
```kotlin
// 1. Package
package com.gusbarros.blockcalls.presentation.home

// 2. Imports (agrupados e ordenados)
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow

// 3. Data classes (se houver)
data class HomeState(val isActive: Boolean)

// 4. Classe principal
class HomeViewModel(app: Application) : AndroidViewModel(app) {
    
    // 4.1. Properties (state primeiro)
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    // 4.2. Init block
    init {
        checkStatus()
    }
    
    // 4.3. Public methods
    fun checkStatus() { }
    
    // 4.4. Private methods
    private fun updateState() { }
    
    // 4.5. Companion object (se houver)
    companion object {
        private const val TAG = "HomeViewModel"
    }
}
```

---

## 🧩 Padrões de Implementação

### ViewModels

**Template**:
```kotlin
class MyViewModel(application: Application) : AndroidViewModel(application) {
    
    // State como StateFlow (imutável externamente)
    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()
    
    // viewModelScope para coroutines
    fun doSomething() {
        viewModelScope.launch {
            // async work
        }
    }
}
```

**Regras**:
- Sempre expor `StateFlow`, nunca `MutableStateFlow`
- Usar `viewModelScope` (cancelamento automático)
- Não fazer I/O diretamente (delegar para Use Cases)

---

### Composables

**Template**:
```kotlin
@Composable
fun MyScreen(
    // 1. Callbacks primeiro
    onNavigate: () -> Unit,
    // 2. ViewModel injetado com default
    viewModel: MyViewModel = viewModel()
) {
    // 3. Collect state FORA do Layout
    val state by viewModel.state.collectAsState()
    
    // 4. Layout
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // UI usando state
    }
}
```

**Regras**:
- State hoisting: callbacks para eventos
- Collect state no topo do Composable
- Modifiers sempre explícitos
- Preview annotations quando fizer sentido

---

### Use Cases

**Template**:
```kotlin
class MyUseCase(
    private val repository: MyRepository
) {
    suspend operator fun invoke(param: String): Result {
        // Lógica de negócio pura
        return repository.doSomething(param)
    }
}
```

**Regras**:
- Operator `invoke` para cases simples
- Retornar tipos do domain (não do data layer)
- Sem dependências Android (exceto annotations)

---

### Repositories

**Interface** (em `domain/repository`):
```kotlin
interface ContactRepository {
    suspend fun isNumberInContacts(phoneNumber: String): Boolean
}
```

**Implementação** (em `data/repository`):
```kotlin
class ContactRepositoryImpl(
    private val contentResolver: ContentResolver
) : ContactRepository {
    
    override suspend fun isNumberInContacts(phoneNumber: String): Boolean {
        return withContext(Dispatchers.IO) {
            // Implementação com Android SDK
        }
    }
}
```

**Regras**:
- Interface sem implementação em `domain`
- Implementação pode usar Android SDK
- Sempre `withContext(Dispatchers.IO)` para I/O

---

## 🛡️ Tratamento de Erros

### Estratégia: Fail-Safe

**CallScreeningService**:
```kotlin
override fun onScreenCall(callDetails: Call.Details) {
    try {
        // validação
    } catch (e: Exception) {
        Log.e(TAG, "Error screening call", e)
        // SEMPRE permitir em caso de erro
        respondToCall(callDetails, allowCall())
    }
}
```

**Repository/UseCase**:
```kotlin
suspend fun doSomething(): Boolean {
    return try {
        // operação
    } catch (e: SecurityException) {
        Log.e(TAG, "Permission denied", e)
        false // retorno seguro
    }
}
```

**Regras**:
- `CallScreeningService`: SEMPRE fail-safe (permitir chamada)
- Repositories: Retornar valores padrão seguros
- ViewModels: Expor erros via State quando relevante para UI

---

## 📝 Comentários e Documentação

### Quando Comentar

**✅ COMENTE**:
```kotlin
// Normalizar remove +55 pois ContentProvider compara sem código de país
val normalized = PhoneNumberNormalizer.normalize(number)

// Fail-safe: em caso de erro, permitir chamada (pode ser emergência)
respondToCall(callDetails, allowCall())

// RoleManager só existe API 29+, verificar antes
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { }
```

**❌ NÃO COMENTE**:
```kotlin
// Criar um estado
val state = MutableStateFlow()

// Chamar o use case
val result = useCase.invoke()
```

### KDoc para APIs Públicas
```kotlin
/**
 * Normaliza números de telefone brasileiros para comparação com ContentProvider.
 * 
 * Remove caracteres especiais e código de país (+55 ou 55 no início).
 * 
 * @param phoneNumber Número no formato original (ex: "+55 11 98765-4321")
 * @return Número normalizado (ex: "11987654321")
 * 
 * @sample
 * ```
 * normalize("+55 11 98765-4321") // "11987654321"
 * normalize("(11) 98765-4321")   // "11987654321"
 * ```
 */
fun normalize(phoneNumber: String): String
```

---

## 🧪 Testes (Fase 2)

### Quando Implementar

**AGORA (MVP)**:
- Estrutura testável (DI, interfaces)
- Comentários `// TODO: Test` onde for crítico

**FASE 2**:
- Testes unitários (domain layer primeiro)
- Testes instrumentados (data layer)

### Template Unit Test
```kotlin
class ValidateContactUseCaseTest {
    
    private lateinit var useCase: ValidateContactUseCase
    private lateinit var repository: ContactRepository
    
    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = ValidateContactUseCase(repository)
    }
    
    @Test
    fun `should return true when number is in contacts`() = runTest {
        // Given
        coEvery { repository.isNumberInContacts(any()) } returns true
        
        // When
        val result = useCase("11987654321")
        
        // Then
        assertTrue(result)
    }
}
```

---

## 🔧 Gradle e Dependências

### Version Catalog

**SEMPRE use**:
```kotlin
// ✅ BOM
implementation(libs.androidx.core.ktx)
implementation(libs.koin.android)

// ❌ RUIM
implementation("androidx.core:core-ktx:1.12.0")
```

### Adicionando Nova Dependência

1. Adicionar em `gradle/libs.versions.toml`:
```toml
[versions]
nova-lib = "1.0.0"

[libraries]
nova-lib = { module = "com.example:nova-lib", version.ref = "nova-lib" }
```

2. Usar em `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.nova.lib)
}
```

---

## 🚀 Workflow de Desenvolvimento

### Ao Implementar Nova Feature

1. **Ler SPEC.md**: Entender requisito
2. **Domain First**: Criar interface + use case
3. **Data Layer**: Implementar repository
4. **Presentation**: ViewModel → Screen
5. **DI**: Registrar no Koin
6. **Validar**: Compilar + lint

### Checklist Antes de Commit

- [ ] Código compila sem warnings
- [ ] Naming conventions respeitadas
- [ ] Comentários em pontos não-óbvios
- [ ] Imports organizados
- [ ] Sem logs de debug esquecidos

---

## 🐛 Debugging

### Logs

**Níveis**:
```kotlin
// Informação útil em desenvolvimento
Log.d(TAG, "Screening call from: $number")

// Erro que não deve acontecer
Log.e(TAG, "Failed to query contacts", exception)

// NUNCA em produção
Log.v(TAG, "Verbose info")
```

**Regras**:
- Sempre usar TAG constante
- Não logar números de telefone completos (privacidade)
- Remover logs verbosos antes de commit

---

## 📦 Git e Commits

### Mensagens de Commit

**Formato**:
```
<tipo>: <descrição curta>

<corpo opcional explicando o POR QUÊ>
```

**Tipos**:
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `refactor`: Mudança de código sem mudar comportamento
- `docs`: Apenas documentação
- `chore`: Tarefas de manutenção (Gradle, CI, etc)

**Exemplos**:
```
feat: implement CallScreeningService for call blocking

Adds the core service that intercepts incoming calls and validates
against contacts before allowing or rejecting the call.

---

fix: normalize phone numbers before contact lookup

ContentProvider comparison fails with country codes, so we now
strip +55 prefix before querying.

---

docs: add architecture decision record for Koin vs Hilt

Chose Koin for simplicity in this MVP showcase project.
```

---

## ❓ Quando Estiver em Dúvida

### Pergunte Antes de Implementar

**Cenários para clarificação**:
- Requisito ambíguo
- Múltiplas abordagens válidas
- Trade-off não claro
- Impacto em outras partes do código

**Como perguntar**:
```
Estou implementando X conforme SPEC.md (RF-001).

Dúvida: Devemos cachear o resultado de isNumberInContacts?

Opções:
A) Não cachear (mais simples, sempre atualizado)
B) Cachear em memória (performance, mas pode ficar desatualizado)

Recomendo A para o MVP, adicionar cache em Fase 2.

Posso prosseguir?
```

---

## 🎯 Objetivos Finais

### Este projeto deve demonstrar:

- ✅ Clean Architecture bem implementada
- ✅ Jetpack Compose moderno
- ✅ Uso correto de APIs Android sensíveis
- ✅ Código limpo e bem documentado
- ✅ CI/CD funcional
- ✅ README que vende o projeto

### Não precisa ter (MVP):

- ❌ Cobertura de teste de 100%
- ❌ Todas features imagináveis
- ❌ Otimização prematura

---

## 📞 Perguntas Frequentes

**P: Posso usar lateinit var?**  
R: Sim, mas prefira injeção via construtor quando possível.

**P: Devo criar custom exceptions?**  
R: Não no MVP. Use exceptions padrão + logs descritivos.

**P: Posso adicionar dependências?**  
R: Pergunte primeiro. MVP deve ser enxuto.

**P: E se eu encontrar um bug na SPEC.md?**  
R: Ótimo! Aponte e sugira correção.

---

**Lembre-se**: Este projeto é uma vitrine de habilidades. Qualidade importa mais que velocidade.

**Última atualização**: 2025-02-10
```

---

## Estrutura Final dos Documentos
```
docs/
├── claude.md           # ← COMO trabalhar (comportamento, convenções)
├── SPEC.md            # ← O QUE construir (requisitos, stack, estrutura)
└── ARCHITECTURE.md    # ← POR QUÊ decisões (ADRs) - criar quando necessário