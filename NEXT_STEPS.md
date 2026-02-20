# 🚀 Próximos Passos - BlockUnknownCalls

> Estrutura base criada com sucesso! Siga este guia para finalizar o MVP.

---

## ✅ O Que Já Está Pronto

### Estrutura Completa
- ✅ Clean Architecture (Domain → Data → Presentation)
- ✅ Gradle com Version Catalog
- ✅ Koin DI configurado
- ✅ Jetpack Compose + Material 3
- ✅ GitHub Actions CI/CD
- ✅ README showcase

### Código Funcional
- ✅ `CallScreeningServiceImpl` - Interceptação de chamadas
- ✅ `ValidateContactUseCase` - Lógica de negócio
- ✅ `ContactRepositoryImpl` - Query de contatos
- ✅ `PhoneNumberNormalizer` - Normalização brasileira
- ✅ `OnboardingScreen` - Setup de permissões
- ✅ `HomeScreen` - Status da proteção

---

## 🔧 Antes de Compilar

### 1. Baixar Gradle Wrapper JAR (CRÍTICO)

O Gradle precisa do arquivo `gradle-wrapper.jar` para funcionar:

```bash
# Opção A: Se você tem Gradle instalado
gradle wrapper

# Opção B: Download manual (escolha uma)
# Via navegador:
# https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar
# Salvar em: gradle/wrapper/gradle-wrapper.jar

# Via curl (se disponível):
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar
```

### 2. Adicionar Ícones do App

O app precisa de ícones. Você pode:

**Opção A - Temporário**: Copiar ícones padrão do Android Studio
```bash
# Se você tiver um projeto Android existente
cp -r /path/to/outro/projeto/app/src/main/res/mipmap-* app/src/main/res/
```

**Opção B - Criar**: Use Image Asset Studio do Android Studio
1. Abrir projeto no Android Studio
2. Right-click em `res` → New → Image Asset
3. Configurar ícone launcher

---

## 🏗️ Build e Execução

### Build Inicial

```bash
# Dar permissão ao gradlew (se necessário)
chmod +x gradlew

# Sync e build
./gradlew clean build

# Ou apenas assemblar
./gradlew assembleDebug
```

### Executar no Dispositivo

```bash
# Conectar dispositivo/emulador e instalar
./gradlew installDebug

# Ou via adb
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🐛 Possíveis Problemas e Soluções

### Erro: "gradle-wrapper.jar not found"
**Solução**: Baixar o JAR conforme seção "Antes de Compilar"

### Erro: "SDK location not found"
**Solução**: Criar `local.properties` na raiz:
```properties
sdk.dir=/Users/SEU_USUARIO/Library/Android/sdk
```

### Erro: Ícones faltando
**Solução**: Adicionar ícones conforme seção "Adicionar Ícones"

### Erro de compilação Compose
**Solução**: Verificar `kotlinCompilerExtensionVersion` em `app/build.gradle.kts`

---

## 📝 Validações Antes do Commit

### Checklist

- [ ] `./gradlew clean build` executa sem erros
- [ ] `./gradlew lintDebug` sem warnings críticos
- [ ] App instala e abre no emulador
- [ ] Onboarding solicita permissões
- [ ] HomeScreen mostra status correto
- [ ] Documentação em `docs/` está completa

### Primeiro Commit

```bash
git init
git add .
git commit -m "feat: initial project setup

- Clean Architecture structure
- CallScreeningService implementation
- Jetpack Compose UI
- Koin DI
- GitHub Actions CI

Implements RF-001 (blocking), RF-002 (onboarding), RF-003 (status).

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>"
```

---

## 🎯 Próximas Features (Fase 2)

### Prioridade Alta
1. **Testes Unitários**
   - `PhoneNumberNormalizerTest`
   - `ValidateContactUseCaseTest`
   - ViewModels com Turbine

2. **Analytics Básico**
   - Room Database para histórico
   - Contador de chamadas bloqueadas

3. **Notificações**
   - Avisar usuário quando chamada é bloqueada
   - Opção de adicionar à whitelist

### Prioridade Média
4. **Whitelist/Blacklist Manual**
   - UI para adicionar números específicos
   - Repository com Room

5. **Settings Screen**
   - Configurações de comportamento
   - Modo "apenas whitelist"

### Prioridade Baixa
6. **Internacionalização**
   - Suporte a números internacionais
   - libphonenumber integration

---

## 📚 Leituras Recomendadas

Antes de continuar, revise:

1. **docs/SPEC.md** - Requisitos completos
2. **docs/CLAUDE.md** - Convenções de código
3. [CallScreeningService Guide](https://developer.android.com/guide/topics/connectivity/telecom/call-screening)
4. [Jetpack Compose Guidelines](https://developer.android.com/jetpack/compose/guidelines)

---

## 🤝 Contribuindo

Se for trabalhar em equipe:

1. Criar branch por feature: `git checkout -b feature/nome`
2. Seguir convenções em `docs/CLAUDE.md`
3. Commit messages formatadas
4. PR para `main` após review

---

## 📞 Precisa de Ajuda?

- **Documentação**: Consulte `docs/SPEC.md` e `docs/CLAUDE.md`
- **Issues**: Abra issue no GitHub com label adequado
- **Discussões**: Use GitHub Discussions para dúvidas gerais

---

**Última Atualização**: 2025-02-10
**Status**: Estrutura MVP Completa - Pronto para Build
