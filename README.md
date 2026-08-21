# Star Client

App simples que verifica se o Minecraft Bedrock (`com.mojang.minecraftpe`) está instalado
e abre o jogo com um clique. Tela de carregamento (splash) + botão "JOGAR".

## Como gerar o .apk

Este .zip contém o **código-fonte completo**, mas não o `.apk` compilado — compilar exige
o Android SDK/Gradle, que não roda no ambiente onde esse projeto foi montado. Duas formas
fáceis de gerar o `.apk`:

### Opção 1 — Android Studio (mais fácil)
1. Baixe e instale o [Android Studio](https://developer.android.com/studio).
2. Abra este projeto (`File > Open` e selecione a pasta `StarClient`).
3. Deixe o Android Studio sincronizar o Gradle (ele baixa e corrige o `gradle-wrapper.jar` sozinho).
4. Vá em `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
5. O `.apk` fica em `app/build/outputs/apk/debug/app-debug.apk`.

### Opção 2 — GitHub Actions (compila na nuvem, sem instalar nada)
1. Crie um repositório no GitHub e suba esse projeto (já vem com `.github/workflows/build.yml`).
2. O GitHub compila automaticamente a cada push na branch `main`.
3. Baixe o `.apk` pronto na aba **Actions > (último build) > Artifacts**.

## Estrutura
- `app/src/main/java/.../MainActivity.kt` — lógica (splash + botão Jogar + abrir Minecraft)
- `app/src/main/res/layout/activity_main.xml` — telas de carregamento e conteúdo
- `app/build.gradle` — configuração do app
