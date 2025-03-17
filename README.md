# Scrapper Bot

Scrapper Bot — это сервис для мониторинга изменений на веб-ресурсах с отправкой уведомлений через Telegram.

## Требования

Перед запуском убедитесь, что у вас установлены:
- **Java 23**
- **Maven 3.9.9**

## Установка и запуск

### 1. Клонирование репозитория

```sh
git clone https://github.com/central-university-dev/java-Therad445
cd java-Therad445
```

### 2. Настройка переменных окружения

Создайте файлы конфигурации для scrapper и бота:

#### scrapper/src/main/resources/application-secrets.yaml

```yaml
app:
  githubToken: "ваш_github_токен"
  stackOverflow:
    key: "ваш_stackoverflow_ключ"
    accessToken: "ваш_stackoverflow_токен"
```

#### bot/src/main/resources/application-secrets.yaml

```yaml
app:
  telegramToken: "ваш_telegram_токен"
```

### 3. Сборка и запуск

#### Сборка проекта

```sh
mvn clean install
```

#### Запуск Scrapper

```sh
java -jar scrapper/target/scrapper.jar
```

#### Запуск бота

```sh
java -jar bot/target/bot.jar
```

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

## Лицензия

MIT License.

