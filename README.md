# yandex-contest-booking — сервис бронирования переговорок

REST-сервис бронирования переговорок на встроенном Jetty с PostgreSQL — решение тестового задания контеста Яндекса. Позволяет создавать брони с проверкой пересечений по времени и просматривать брони по пользователю или по переговорке.

## Возможности

- `GET /ping` — health-check, возвращает `{"status":"ok"}`
- `POST /book` — создание брони; проверяет корректность параметров и пересечение интервалов по переговорке (конфликт → `409 CONFLICT`)
- `GET /booklist` — список броней в JSON (Gson) по `user_id` или `place_id`
- Пул соединений HikariCP, PreparedStatement против SQL-инъекций
- Конфигурация БД через переменные окружения

## API

| Метод | Путь | Параметры | Ответы |
|---|---|---|---|
| GET | `/ping` | — | `200 {"status":"ok"}` |
| POST | `/book` | `place_id`, `user_id` (int), `from`, `to` (ISO-8601 instant, напр. `2024-01-01T10:00:00Z`) | `200` создано; `400` — неверные параметры или `from >= to`; `409` — пересечение с существующей бронью |
| GET | `/booklist` | `user_id` или `place_id` | `200 {"bookings":[...]}`; без параметров — пустой список |

## Технологии

- Java, Gradle 8, JDK 21 (сборка), Jakarta Servlet API 6.0.0
- Jetty Server / Servlet 11.0.20 (встроенный HTTP-сервер)
- PostgreSQL (драйвер 42.7.2) + HikariCP 5.1.0
- Gson 2.10.1, SLF4J Simple 2.0.12

## Запуск

Переменные окружения БД (со значениями по умолчанию): `DB_HOST=localhost`, `DB_PORT=5432`, `DB_USER=postgres`, `DB_PASSWORD=postgres`, `DB_NAME=contest`.

Минимальная схема — таблица `bookings` с колонками `id`, `user_id int`, `place_id int`, `time_from`, `time_to` (timestamp), создаваемая во внешней БД.

```bash
./gradlew build --no-daemon
./gradlew run --no-daemon --args='--port 8080'
```

Так же сервис тестируется на контесте: в Docker-образе `gradle:8-jdk21` с PostgreSQL 16.1 из корня репозитория вызываются указанные выше команды.

Пример запроса:

```bash
curl -X POST 'http://localhost:8080/book?place_id=1&user_id=2&from=2024-01-01T10:00:00Z&to=2024-01-01T11:00:00Z'
curl 'http://localhost:8080/booklist?place_id=1'
```

## Структура проекта

```
src/main/java/ru/kuzdikenov/booking/
├── Main.java                    # точка входа: порт (--port), Jetty, маршруты
├── servlet/
│   ├── PingServlet.java         # GET /ping
│   ├── BookServlet.java         # POST /book: валидация + проверка конфликтов
│   └── BookListServlet.java     # GET /booklist: JSON через Gson
├── dao/BookingDao.java          # SQL: create, hasConflict, findByUserId/PlaceId
├── model/Booking.java           # модель брони (id, user, place, интервал)
└── util/DatabaseConfig.java     # HikariCP-пул, env-переменные
```
