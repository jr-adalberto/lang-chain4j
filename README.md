# 🚗 AutoSeminovos AI Assistant

Assistente de IA para loja de veículos usados e seminovos, construído com **Java 21**, **Spring Boot 3.5** e **LangChain4j**.

O assistente integra o LLM **Gemini 2.5 Flash** (Google AI Studio) com ferramentas internas da aplicação, decidindo autonomamente quando acionar cada ferramenta com base no contexto da conversa.

---

## 🧱 Arquitetura

```
Cliente (Postman / Frontend)
        │
        ▼
AssistantController  (POST /api/assistant)
        │
        ▼
SeminovosAiService   (interface declarativa LangChain4j)
        │
   ┌────┴────────────────────┐
   ▼                         ▼
Gemini LLM              SeminovosTools
(resposta contextual)   (ferramentas internas)
                         ├── consultarEstoque()
                         ├── simularFinanciamento()
                         └── agendarTestDrive()
```

---

## ⚙️ Tecnologias

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 21 (LTS) | Linguagem principal |
| Spring Boot | 3.5.0 | Framework web e IoC |
| LangChain4j | 1.7.1-beta14 | Bridge Java ↔ LLM |
| Gemini 2.5 Flash | - | Modelo de linguagem (LLM) |
| Maven | 3.x | Gerenciamento de dependências |

---

## 🛠️ Ferramentas Internas (Tools)

As ferramentas são métodos Java anotados com `@Tool` do LangChain4j.
O LLM decide **automaticamente** quando acionar cada uma com base na mensagem do usuário.

### 1. `consultarEstoque`
Filtra o estoque de veículos por marca, modelo, ano mínimo ou preço máximo.

> **Acionado quando:** cliente pergunta sobre carros disponíveis, preços, opções de modelos.

### 2. `simularFinanciamento`
Calcula parcelas usando a **Tabela Price** com taxa de 1,49% a.m. (referência BACEN).
Retorna parcela mensal, total pago e CET estimado.

> **Acionado quando:** cliente menciona financiamento, parcelas, entrada, crédito.

### 3. `agendarTestDrive`
Registra o agendamento com nome do cliente, modelo desejado e data.
Gera protocolo de confirmação automático.

> **Acionado quando:** cliente quer fazer um test-drive.

---

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.8+
- Chave de API do Google AI Studio (gratuita)

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/seminovos-ai-assistant.git
cd seminovos-ai-assistant
```

### 2. Gere sua chave de API

Acesse [https://aistudio.google.com](https://aistudio.google.com), faça login com sua conta Google e crie uma API Key.

### 3. Configure o `application.properties`

Copie o arquivo de exemplo e preencha com sua chave:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

```properties
gemini.api.key=SUA_CHAVE_AQUI
gemini.model=gemini-2.5-flash
server.port=8080
```

### 4. Execute

```bash
mvn spring-boot:run
```

A aplicação sobe na porta `8080`.

---

## 📡 Endpoints

### `POST /api/assistant`

**Request:**
```json
{
  "message": "Quero simular o financiamento de um Corolla de R$ 115.000 com R$ 30.000 de entrada em 48 meses"
}
```

**Response:**
```json
{
  "response": "💰 Simulação de Financiamento — AutoSeminovos\n\nValor do veículo: R$ 115.000,00\nEntrada: R$ 30.000,00 (26,1%)\n..."
}
```

---

## 💬 Exemplos de Conversa

| Pergunta do Cliente | Tool Acionada | Comportamento |
|---|---|---|
| "Quais carros vocês têm?" | `consultarEstoque` | Lista todo o estoque |
| "Tem SUV abaixo de R$ 130.000?" | `consultarEstoque` | Filtra por categoria e preço |
| "Simule 48x do Corolla de R$ 115k com R$ 30k de entrada" | `simularFinanciamento` | Calcula parcelas com Tabela Price |
| "Quero agendar test-drive do Civic para 10/04/2026" | `agendarTestDrive` | Gera protocolo de agendamento |
| "Quais documentos preciso para transferir um carro?" | *(nenhuma)* | Responde direto via LLM |
| "O que é IPVA?" | *(nenhuma)* | Responde direto via LLM |

---

## 🔄 Fluxo de Decisão do LLM

```
Mensagem do usuário
       │
       ▼
  SeminovosAiService
       │
       ├─► Envia para o Gemini com System Prompt
       │
       ▼
  Gemini avalia o contexto
       │
       ├── Precisa de ferramenta? ──► Aciona Tool ──► Resultado ──► Resposta final
       │
       └── Não precisa? ──────────────────────────────────────────► Resposta direta
```

---

## 📁 Estrutura do Projeto

```
src/main/java/com/langchain/
├── LangChainApplication.java       # Classe principal
├── controller/
│   └── AssistantController.java    # POST /api/assistant
├── service/
│   └── SeminovosAiService.java     # Interface declarativa @AiService
├── tools/
│   └── SeminovosTools.java         # 3 ferramentas internas com @Tool
└── config/
    └── AssistantConfig.java        # Configuração do LLM e registro das tools
```

---

## ⚠️ Observações Técnicas

- **Versão do LangChain4j:** utilizada a `1.7.1-beta14`. Nessa versão o método de configuração do modelo é `.chatModel()` (não `.chatLanguageModel()` como nas versões anteriores).
- **Registro das Tools:** nessa versão, as tools devem ser registradas explicitamente via `AiServices.builder().tools(...)` no `AssistantConfig`. O scanner automático do `@AiService` conflita com o registro manual.
- **Modelo Gemini:** utilizar `gemini-2.5-flash`. Modelos com sufixo de preview podem não estar disponíveis dependendo da conta/região.

---

## 📌 Melhorias Futuras

- [ ] Adicionar `@MemoryId` para manter histórico de conversa por sessão
- [ ] Persistir estoque e agendamentos em banco de dados (PostgreSQL + Spring Data JPA)
- [ ] Integrar com API da tabela FIPE para precificação dinâmica
- [ ] Implementar testes unitários com JUnit 5 nas ferramentas internas
- [ ] Criar frontend com chat em tempo real

---

## 👨‍💻 Autor

Desenvolvido como projeto de estudo de **Java + IA + LangChain4j**.

---

## 📄 Licença

MIT
