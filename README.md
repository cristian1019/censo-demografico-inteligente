# Censo Demográfico Inteligente

Aplicativo Android que mostra **os nomes mais comuns de homens e mulheres (e os sobrenomes mais comuns)** de cada estado ou município brasileiro, usando os dados do **Censo 2022 do IBGE**.

Atividade da disciplina **Mobile Technologies** (UNIS — Ciência da Computação, 2026/2).

## 👤 Integrante

- Cristian Henrique Souza Jeronimo

## 🎬 Vídeo de demonstração

[Link do vídeo](COLOQUE_AQUI_O_LINK_DO_VIDEO)

## 📱 Funcionalidades

1. **Seleção de localidade:** ao abrir, o app consome a API de Localidades do IBGE e preenche um dropdown com todos os estados. Depois de escolher o estado, um segundo dropdown traz os municípios dele (opcional).
2. **Consulta encadeada:** ao selecionar o estado ou o município, o `id` da localidade é usado automaticamente para consultar a API de Nomes do IBGE.
3. **Exibição dos dados:** o resultado é separado em três abas: **Mulheres**, **Homens** e **Sobrenomes**, com a posição no ranking e a quantidade de pessoas.

## 🌐 APIs utilizadas

| API | Endpoint |
|---|---|
| Localidades (estados) | `https://servicodados.ibge.gov.br/api/v1/localidades/estados` |
| Localidades (municípios de um estado) | `https://servicodados.ibge.gov.br/api/v1/localidades/estados/{id_estado}/municipios` |
| Nomes — Censo 2022 | `https://servicodados.ibge.gov.br/api/v3/nomes/2022/localidade/{id_localidade}` |

Documentação: https://servicodados.ibge.gov.br/api/docs

## 🏗️ Arquitetura em camadas

```
app/src/main/java/br/edu/unis/censo/
├── MainActivity.kt                  → ponto de entrada do app
├── model/                           → CAMADA DE MODELOS
│   ├── Localidade.kt                  (Estado, Municipio)
│   └── Nome.kt                        (NomeFrequencia, RankingNomes)
├── service/                         → CAMADA DE SERVIÇOS / INTEGRAÇÃO
│   ├── HttpClient.kt                  (requisições HTTP GET)
│   ├── IbgeLocalidadesService.kt      (API de Localidades + conversão do JSON)
│   └── IbgeNomesService.kt            (API de Nomes + conversão do JSON)
└── ui/                              → CAMADA DE APRESENTAÇÃO
    ├── CensoScreen.kt                 (dropdowns, abas e lista de nomes em Compose)
    ├── CensoViewModel.kt              (estado da tela; chama os serviços)
    └── theme/Theme.kt
```

- **Apresentação (`ui`)**: só desenha a tela e repassa as ações do usuário ao `CensoViewModel`. Não faz requisições.
- **Serviços (`service`)**: fazem as chamadas HTTP às APIs do IBGE e transformam o JSON nos modelos.
- **Modelos (`model`)**: classes de dados que representam estados, municípios, nomes e frequências.

## 🛠️ Tecnologias

- Kotlin
- Jetpack Compose (Material 3)
- ViewModel + StateFlow
- Coroutines (requisições fora da thread principal)
- `HttpURLConnection` e `org.json` (nativos do Android, sem bibliotecas extras)

## ▶️ Como executar

1. Instale o **Android Studio** (Koala ou mais recente) com o SDK do Android 14 (API 34).
2. Clone o repositório:
   ```bash
   git clone https://github.com/cristian1019/censo-demografico-inteligente.git
   ```
3. No Android Studio, vá em **File > Open** e selecione a pasta do projeto. Aguarde o Gradle sincronizar.
4. Crie ou inicie um emulador (**Device Manager**) ou conecte um celular com a depuração USB ativada.
5. Clique em **Run ▶** (ou `Shift + F10`).

O app precisa de internet para consultar as APIs do IBGE.

Para gerar o APK pela linha de comando:

```bash
./gradlew assembleDebug
# APK gerado em app/build/outputs/apk/debug/app-debug.apk
```
