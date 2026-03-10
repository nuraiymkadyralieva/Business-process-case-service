# Business Process Service

Backend-сервис для управления жизненным циклом бизнес-процессов (дел). Система реализует контролируемую статусную модель, хранит историю изменений, управляет участниками и документами, обеспечивает прозрачную бизнес-логику переходов между состояниями.


## Стек технологий

| Технология | Назначение |
|---|---|
| Java 17 | Основной язык разработки |
| Spring Boot | Backend-фреймворк |
| Spring Data JPA + Hibernate | ORM и работа с БД |
| PostgreSQL | Реляционная база данных |
| Spring Validation | Валидация входных данных |
| Swagger / OpenAPI | Документация API |
| Maven | Сборка и управление зависимостями |

## Архитектура

Приложение построено по слоистой архитектуре. Каждый слой имеет строго ограниченную зону ответственности.

```
Controller → Service → Repository → Database
```

**Controller** — принимает HTTP-запросы, валидирует DTO, вызывает сервисы, формирует ответы. Бизнес-логики не содержит.

**Service** — основная бизнес-логика: правила переходов статусов, бизнес-валидация, транзакционные операции, автоматические действия при смене статуса.

**Repository** — доступ к базе данных через Spring Data JPA: поиск, сохранение, удаление, фильтрация, пагинация.

**DTO** — объекты передачи данных, отделяющие внутреннюю модель от API.


## Доменная модель

### Case (дело)
Центральная сущность системы. Представляет отдельный бизнес-процесс.

| Поле | Описание |
|---|---|
| `id` | Уникальный идентификатор |
| `caseNumber` | Номер дела |
| `procedureType` | Тип процедуры |
| `status` | Текущий статус |
| `startDate` | Дата начала |
| `endDate` | Дата завершения |

Связанные сущности: участники, документы, история статусов.

### Party (участник)
Участник бизнес-процесса — физическое или юридическое лицо. Характеризуется типом, отображаемым именем и ролью.

### PartyRole (роль участника)
```
DEBTOR | CREDITOR | APPLICANT | RESPONDENT
```

### ProcedureType (тип процедуры)
```
BANKRUPTCY | COURT_PROCEEDING | DOCUMENT_VERIFICATION
```

### Document (документ)
Документ, связанный с делом. Используется для подтверждения этапов процесса.

### StatusHistory (история статусов)

| Поле | Описание |
|---|---|
| `caseId` | Идентификатор дела |
| `previousStatus` | Предыдущий статус |
| `newStatus` | Новый статус |
| `changedAt` | Дата изменения |
| `initiatedBy` | Инициатор изменения |


## Статусная модель

### Жизненный цикл дела

```
CREATED → IN_PROGRESS → PROCEDURE_RUNNING → COMPLETED → ARCHIVED
```

Любой переход, не входящий в эту цепочку, блокируется системой.

### Бизнес-ограничения переходов

| Переход | Условие |
|---|---|
| `IN_PROGRESS → PROCEDURE_RUNNING` | Наличие хотя бы одного участника |
| `PROCEDURE_RUNNING → COMPLETED` | Наличие хотя бы одного документа |

### Автоматические действия при смене статуса

- **Запись истории** — каждый переход фиксируется в `StatusHistory`
- **Пересчёт метрик** — stub, расширяется при необходимости
- **Отправка уведомлений** — stub, с возможностью интеграции с email / message broker / event system


## REST API

### Дела

| Метод | Endpoint | Описание |
|---|---|---|
| `GET` | `/api/cases` | Список дел (с пагинацией и фильтрацией) |
| `GET` | `/api/cases/{id}` | Получить дело |
| `POST` | `/api/cases` | Создать дело |
| `PATCH` | `/api/cases/{id}` | Обновить дело |
| `DELETE` | `/api/cases/{id}` | Удалить дело |
| `POST` | `/api/cases/{id}/status` | Изменить статус |
| `GET` | `/api/cases/{id}/history` | История статусов |

Удаление допускается только при статусе `CREATED`, отсутствии участников и документов.

### Фильтрация и пагинация

```
GET /api/cases?page=0&size=10
GET /api/cases?status=CREATED
GET /api/cases?caseNumber=CASE-001
```

### Примеры запросов

**Создание дела:**
```http
POST /api/cases
Content-Type: application/json

{
  "caseNumber": "CASE-001",
  "procedureType": "BANKRUPTCY"
}
```

**Изменение статуса:**
```http
POST /api/cases/{id}/status
Content-Type: application/json

{
  "newStatus": "IN_PROGRESS",
  "initiatedBy": "system"
}
```

### Обработка ошибок

| Код | Описание |
|---|---|
| `400` | Ошибка валидации |
| `403` | Недопустимый переход статуса |
| `404` | Дело не найдено |


## Запуск проекта

### Требования

- Java 17
- PostgreSQL
- Maven

### Настройка БД

```sql
CREATE DATABASE business_process_db;
```

### `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/business_process_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Сборка и запуск

```bash
mvn clean install
mvn spring-boot:run
```

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

## Структура проекта

```
src/main/java/
├── controllers/   # HTTP-слой, маппинг запросов
├── service/       # Бизнес-логика, переходы статусов
├── repository/    # Spring Data JPA репозитории
├── domain/        # JPA-сущности
├── dto/           # Объекты передачи данных
└── error/         # Централизованная обработка ошибок
```
