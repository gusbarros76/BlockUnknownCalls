# 📱 Guia de Instalação - Block Unknown Calls

## 📋 Pré-requisitos

Antes de instalar o app, certifique-se de ter:

- ✅ **Build completo**: APK gerado via `./gradlew assembleDebug`
- ✅ **Dispositivo Android** com:
  - Android 10+ (API 29+)
  - Depuração USB habilitada (para instalação via adb)
- ✅ **Android SDK Platform Tools** instalado (para adb)

---

## 🚀 Método 1: Instalação via Gradle (Recomendado)

### Passo a Passo

1. **Conectar dispositivo via USB**
   ```bash
   # Habilitar no dispositivo:
   # Configurações → Sobre o telefone → Build number (tocar 7x)
   # Configurações → Opções do desenvolvedor → Depuração USB (ativar)
   ```

2. **Verificar conexão**
   ```bash
   adb devices
   # Deve mostrar: List of devices attached
   #                ABC123XYZ    device
   ```

3. **Instalar app**
   ```bash
   cd /Users/GusBarros/Projects/BlockUnknownCalls
   ./gradlew installDebug
   ```

4. **Sucesso!** 🎉
   ```
   BUILD SUCCESSFUL
   Installed on 1 device(s).
   ```

---

## 🔧 Método 2: Instalação via ADB

### Se Gradle der problema

1. **Localizar APK**
   ```bash
   ls -lh app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Instalar via adb**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

   Flags úteis:
   - `-r`: Reinstala se já existir
   - `-t`: Permite instalar test APKs
   - `-d`: Downgrade permitido

---

## 📂 Método 3: Transferência Manual

### Para dispositivos sem cabo USB

1. **Copiar APK para o dispositivo**
   ```bash
   # Via Google Drive/Dropbox/Email
   # Localizar: app/build/outputs/apk/debug/app-debug.apk
   ```

2. **No dispositivo Android**:
   - Abrir arquivo `.apk` baixado
   - Se necessário: Configurações → Segurança → "Fontes desconhecidas" (ativar)
   - Confirmar instalação

---

## 🎯 Configuração Inicial (Primeira Abertura)

### Etapa 1: Permissões Runtime

O app solicitará 3 permissões:

1. **📇 Contatos** - Para verificar se número está salvo
   - Toque em "Conceder Permissões"
   - Aceite todas as 3 permissões:
     - ✅ Contatos
     - ✅ Telefone
     - ✅ Registros de chamadas

### Etapa 2: Call Screening Role

2. **☎️ Filtro de Chamadas** - Configuração manual obrigatória
   - Toque em "Configurar Bloqueio de Chamadas"
   - Android abrirá configurações do sistema
   - Selecione **"Block Unknown Calls"** como app de filtro
   - Confirme

### Etapa 3: Verificação

3. **✅ Status Ativo**
   - Após configurar, app navega automaticamente para tela Home
   - Card verde: "Proteção Ativa" ✓
   - Se vermelho: Revise permissões nas configurações

---

## 🧪 Testar Funcionalidade

### Teste 1: Chamada de Contato (Deve Permitir)

1. Salve um número nos contatos (ex: seu próprio celular)
2. Ligue desse número para o dispositivo
3. **Esperado**: Chamada toca normalmente ✅

### Teste 2: Chamada Desconhecida (Deve Bloquear)

1. Use outro número NÃO salvo nos contatos
2. Ligue para o dispositivo
3. **Esperado**: Chamada bloqueada, não toca 🚫
4. Verifique no registro de chamadas: aparece como "bloqueada"

---

## 🐛 Troubleshooting

### App não abre após instalação

```bash
# Ver logs de erro
adb logcat | grep BlockUnknownCalls

# Verificar se app está instalado
adb shell pm list packages | grep blockcalls
```

**Solução comum**: Desinstalar e reinstalar
```bash
adb uninstall com.gusbarros.blockcalls
./gradlew installDebug
```

---

### Permissões não solicitadas

**Causa**: Permissões já negadas anteriormente

**Solução**:
```bash
# Via adb
adb shell pm grant com.gusbarros.blockcalls android.permission.READ_CONTACTS
adb shell pm grant com.gusbarros.blockcalls android.permission.READ_PHONE_STATE
adb shell pm grant com.gusbarros.blockcalls android.permission.READ_CALL_LOG

# Ou manualmente no dispositivo
# Configurações → Apps → Block Unknown Calls → Permissões → Ativar todas
```

---

### CallScreeningRole não disponível

**Causa**: Android < 10 (API < 29)

**Verificar versão**:
```bash
adb shell getprop ro.build.version.sdk
# Deve ser >= 29
```

**Solução**: Use dispositivo com Android 10+

---

### Chamadas não estão sendo bloqueadas

**Checklist de diagnóstico**:

1. **Verificar permissões**
   ```bash
   adb shell dumpsys package com.gusbarros.blockcalls | grep permission
   # Todas devem estar granted
   ```

2. **Verificar CallScreeningRole**
   ```bash
   adb shell dumpsys role | grep ROLE_CALL_SCREENING
   # Deve mostrar: com.gusbarros.blockcalls
   ```

3. **Verificar logs do serviço**
   ```bash
   adb logcat -s CallScreeningService
   # Deve mostrar: "Screening call from: ..."
   ```

4. **Reiniciar dispositivo**
   ```bash
   adb reboot
   ```

---

### Build falha com "Execution failed for task ':app:packageDebug'"

**Causa**: Múltiplas variantes ou erro de assinatura

**Solução**:
```bash
# Limpar e rebuild
./gradlew clean
./gradlew assembleDebug --stacktrace

# Se persistir, verificar AndroidManifest.xml
```

---

### "Installation failed with message INSTALL_FAILED_UPDATE_INCOMPATIBLE"

**Causa**: App já instalado com assinatura diferente

**Solução**:
```bash
# Desinstalar versão anterior
adb uninstall com.gusbarros.blockcalls

# Reinstalar
./gradlew installDebug
```

---

## 📊 Informações da Build

Após `./gradlew assembleDebug`:

```bash
# Localização do APK
app/build/outputs/apk/debug/app-debug.apk

# Ver detalhes do APK
./gradlew app:assembleDebug --info | grep "APK"

# Tamanho típico esperado
# ~3-5 MB (MVP sem bibliotecas pesadas)
```

---

## 🔄 Atualizar App (Após Mudanças no Código)

```bash
# Rebuild e reinstall
./gradlew clean installDebug

# Ou forçar reinstalação
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📝 Desinstalação

### Via adb
```bash
adb uninstall com.gusbarros.blockcalls
```

### Via dispositivo
```
Configurações → Apps → Block Unknown Calls → Desinstalar
```

---

## 🎯 Checklist Completo de Instalação

- [ ] JDK 17 instalado
- [ ] Build executado: `./gradlew assembleDebug`
- [ ] APK gerado em `app/build/outputs/apk/debug/`
- [ ] Dispositivo conectado: `adb devices`
- [ ] App instalado: `./gradlew installDebug`
- [ ] App abre sem crashes
- [ ] Permissões concedidas (3x)
- [ ] CallScreeningRole configurado
- [ ] Status mostra "Proteção Ativa" (verde)
- [ ] Teste com chamada de contato: ✅ permite
- [ ] Teste com número desconhecido: 🚫 bloqueia

---

## 📞 Próximos Passos

Após instalação bem-sucedida:

1. ✅ **Uso normal**: App funciona em background
2. 🧪 **Testar edge cases**: números internacionais, privados, etc
3. 📊 **Monitorar logs**: `adb logcat -s CallScreeningService`
4. 🚀 **Fase 2**: Implementar features adicionais (ver `NEXT_STEPS.md`)

---

**Última Atualização**: 2025-02-10
**Status**: Pronto para instalação (requer JDK 17)
