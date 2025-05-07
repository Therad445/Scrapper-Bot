# Scrapper Bot

**Scrapper & Bot** — двух‑сервисное приложение, которое

1. периодически проверяет подписанные ссылки (GitHub PR/Issue, Stack Overflow Answer/Comment) и обнаруживает обновления;
2. отправляет превью изменений в Telegram‑чат.

Scrapper хранит все данные в PostgreSQL и умеет работать **как «голым» SQL, так и через ORM (Spring Data JPA)** — выбор
задаётся конфигурацией.

---

## Требования

Перед запуском убедитесь, что у вас установлены:

- **Java 23**
- **Maven 3.9.9**
- **Docker + Docker Compose (для локальной БД)**

## Содержимое репозитория

```
java‑Therad445Clear/
├── bot/                 – REST‑API + Telegram‑bot
├── scrapper/            – планировщик, сервис доступа к БД
├── migrations/          – Liquibase‑схема БД (master.xml)
├── docker‑compose.yaml  – Postgres + оба сервиса
└── pom.xml              – multimodule Maven‑build
```

---

## 📦 Быстрый старт (через Docker Compose)

### 1. Клонируйте репозиторий

```bash
git clone https://github.com/central-university-dev/java-Therad445.git
cd java-Therad445
```

### 2. Установите переменные окружения

```bash
export GITHUB_TOKEN=your_github_token
export SO_TOKEN_KEY=your_stackoverflow_key
export SO_ACCESS_TOKEN=your_stackoverflow_access_token
export TELEGRAM_TOKEN=your_telegram_bot_token
```

> Эти переменные автоматически попадут в контейнеры через `docker-compose.yaml`.

### 3. Соберите и запустите все сервисы

```bash
docker compose up --build
```

Запустятся:

- `postgresql` (порт 5432)
- `scrapper` (порт 8081, проверяет обновления и шлёт их боту)
- `bot` (порт 8080, REST API и Telegram-интеграция)

Scrapper подождёт доступности БД, а bot — готовности scrapper (через HEALTHCHECK).

---

## 🔧 Переменные окружения

|    переменная     | используется в |         назначение         |
|-------------------|----------------|----------------------------|
| `GITHUB_TOKEN`    | scrapper       | GitHub API access          |
| `SO_TOKEN_KEY`    | scrapper       | StackOverflow API key      |
| `SO_ACCESS_TOKEN` | scrapper       | StackOverflow access token |
| `TELEGRAM_TOKEN`  | bot            | Telegram Bot API token     |

---

## Конфигурация scrapper

Параметры задаются через переменные окружения и `application.yaml`.

<details>
<summary><code>scrapper/src/main/resources/application.yaml</code></summary>

```yaml
app:
    github-token: ${GITHUB_TOKEN}
    stackoverflow:
        key: ${SO_TOKEN_KEY}
        access-token: ${SO_ACCESS_TOKEN}
    scheduler:
        enable: true
        interval: 30s
        force-check-delay: 15m
        batch-size: 100
        thread-count: 4
    notification:
        type: http
    access-type: SQL    # или ORM

spring:
    application:
        name: Scrapper
    datasource:
        url: jdbc:postgresql://postgres:5432/scrapper
        username: postgres
        password: postgres
    liquibase:
        enabled: false     # включите true, если хотите авто-миграции
    jpa:
        hibernate:
            ddl-auto: none
        open-in-view: false

server:
    port: 8081

springdoc:
    swagger-ui:
        enabled: true
        path: /swagger-ui
```

</details>

Перед запуском задайте переменные окружения:

```bash
export GITHUB_TOKEN=<your_github_token>
export SO_TOKEN_KEY=<your_so_key>
export SO_ACCESS_TOKEN=<your_so_token>
```

---

## Конфигурация бота

Файл конфигурации бота `bot/src/main/resources/application.yaml`:

<details>
<summary><code>bot/src/main/resources/application.yaml</code></summary>

```yaml
app:
    telegram-token: ${TELEGRAM_TOKEN}

spring:
    application:
        name: Bot
    liquibase:
        enabled: false
    jpa:
        hibernate:
            ddl-auto: validate
        open-in-view: false

server:
    port: 8080

springdoc:
    swagger-ui:
        enabled: true
        path: /swagger-ui
```

</details>

Перед запуском бота необходимо задать переменную окружения:

```bash
export TELEGRAM_TOKEN=<your_telegram_token>
```

---

## Сборка и запуск

> ⚠️ **Важно!** Перед сборкой убедитесь, что Docker daemon запущен.  
> Проект полагается на контейнер PostgreSQL; без него сборка/запуск упадут.

```bash
mvn clean package -DskipTests
java -jar scrapper/target/scrapper.jar   # планировщик + HTTP API
java -jar bot/target/bot.jar            # Telegram‑bot
```

> Оба приложения запускаются «из IDE» одной кнопкой – главное, чтобы контейнер Postgres уже был запущен.

---

## Конфигурация планировщика

|             ключ             | по умолчанию |               описание               |
|------------------------------|--------------|--------------------------------------|
| `app.scheduler.interval`     | `30s`        | частота проверки ссылок              |
| `app.scheduler.batch-size`   | `100`        | сколько ссылок сканировать за проход |
| `app.scheduler.thread-count` | `4`          | параллельных потоков внутри батча    |

---

## Запуск тестов

Интеграционные тесты поднимают **Testcontainers Postgres 15**:

```bash
mvn verify
```

Проверяется:

* CRUD‑операции репозиториев (SQL и ORM);
* корректный выбор имплементации по `access-type`;
* формирование превью для GitHub / Stack Overflow.

---

## Использование

После запуска сервис доступен по адресу: `http://localhost:8081`.

Для тестирования API можно использовать `curl` или Postman. Пример запроса:

```sh
curl --location 'http://localhost:8081/links' \
--header 'Tg-chat-id: 99' \
--header 'Content-Type: application/json' \
--data '{
  "link": "https://example.com/",
  "tags": [
    "string"
  ],
  "filters": [
    "string"
  ]
}'
```

