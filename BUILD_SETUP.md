# 🔧 Build Setup - BlockUnknownCalls

## ⚠️ Problema Detectado

O build falhou porque **Java/JDK não está instalado** no sistema.

```
Error: Unable to locate a Java Runtime
```

Este projeto requer **JDK 17** (conforme `app/build.gradle.kts`).

---

## ✅ Solução: Instalar JDK 17

### Opção 1: Homebrew (Recomendado para macOS)

```bash
# Instalar Homebrew (se não tiver)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar JDK 17 (Temurin/Eclipse Adoptium)
brew install --cask temurin@17

# Verificar instalação
java -version
# Deve mostrar: openjdk version "17.x.x"
```

### Opção 2: Download Direto

1. Acesse: https://adoptium.net/temurin/releases/?version=17
2. Selecione:
   - **Version**: 17 - LTS
   - **Operating System**: macOS
   - **Architecture**: x64 (ou aarch64 se M1/M2/M3)
   - **Package Type**: JDK
3. Baixe e instale o `.pkg`

### Opção 3: SDKMAN (Gerenciador de SDKs)

```bash
# Instalar SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Instalar JDK 17
sdk install java 17.0.9-tem

# Definir como padrão
sdk default java 17.0.9-tem
```

---

## 🔍 Verificar Instalação

Após instalar, verifique:

```bash
# Versão do Java
java -version
# Esperado: openjdk version "17.x.x"

# JAVA_HOME (deve estar definido)
echo $JAVA_HOME
# Esperado: /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

# Localizar Java homes disponíveis (macOS)
/usr/libexec/java_home -V
```

---

## 🚀 Após Instalar Java

Execute o build novamente:

```bash
# Voltar ao diretório do projeto
cd /Users/GusBarros/Projects/BlockUnknownCalls

# Build completo
./gradlew clean build

# Ou apenas debug APK
./gradlew assembleDebug
```

---

## 🐛 Troubleshooting

### "JAVA_HOME not set"

```bash
# Adicionar ao ~/.zshrc ou ~/.bash_profile
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

# Recarregar shell
source ~/.zshrc  # ou source ~/.bash_profile
```

### "Unsupported class file major version"

Significa que o Java instalado é mais antigo que o necessário.

```bash
# Desinstalar versão antiga e instalar JDK 17
brew uninstall openjdk
brew install --cask temurin@17
```

### "Gradle version incompatible"

O projeto usa Gradle 8.2, que requer JDK 17+.

```bash
# Verificar versão do Gradle
./gradlew --version

# Deve mostrar:
# Gradle 8.2
# JVM: 17.x.x
```

---

## 📋 Requisitos Mínimos

| Componente | Versão Mínima |
|------------|---------------|
| **JDK** | 17 (LTS) |
| **Gradle** | 8.2 (via wrapper) |
| **Android SDK** | API 29-34 |
| **Android Studio** | Hedgehog (opcional) |

---

## ✅ Checklist Pré-Build

Antes de executar `./gradlew build`:

- [ ] JDK 17+ instalado
- [ ] `java -version` mostra versão 17+
- [ ] `JAVA_HOME` está definido corretamente
- [ ] `gradle-wrapper.jar` existe em `gradle/wrapper/`
- [ ] Permissões de execução: `chmod +x gradlew`

---

## 🎯 Próximo Passo

Após instalar JDK 17 e verificar a instalação, retorne ao Claude Code e reexecute:

```bash
./gradlew clean build
```

---

**Última Atualização**: 2025-02-10
**Status**: Aguardando instalação do JDK 17
