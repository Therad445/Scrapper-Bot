# Scrapper Bot

Scrapper Bot — это сервис для мониторинга изменений на веб-ресурсах с отправкой уведомлений через Telegram.

## Требования

Перед запуском убедитесь, что у вас установлены:
- **Java 17+**
- **Maven 3.8+**

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

После запуска сервис доступен по адресу: `http://localhost:8080`.

Для тестирования API можно использовать `curl` или Postman. Пример запроса:

```sh
curl -X POST http://localhost:8080/api/links \
     -H "Content-Type: application/json" \
     -d '{"chatId": 100, "url": "https://example.com"}'
```

## Лицензия

MIT License.

