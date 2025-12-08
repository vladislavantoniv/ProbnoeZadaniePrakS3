🏥 Лабораторная информационная система (ЛИС)
Система для управления пациентами, заявками на анализы и результатами исследований в медицинской лаборатории.
Базовый REST API для Лабораторной информационной системы (ЛИС), которая позволяет регистрировать пациентов, 
создавать заявки на лабораторные исследования и получать результаты анализов.
Основные модули:
Пациенты - управление данными пациентов (CRUD, поиск)
Заявки - создание и отслеживание заявок на анализы
Типы анализов - справочник лабораторных исследований
Анализы - управление результатами исследований

Технологический стек

Java 17
Spring Boot 3.2.0
Spring Data JPA
Spring Validation
PostgreSQL
Flyway (миграции)
JUnit 5 (тестирование)
Maven
Swagger (документация API)
H2 (тестовая БД)
Структура проекта


====
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── lab/
│   │           ├── controller/  # REST контроллеры
│   │           ├── dto/         # DTO объекты
│   │           ├── entity/      # Entity объекты
│   │           ├── repository/  # Репозитории
│   │           ├── service/     # Сервисы
│   │           ├── exception/   # Исключения
│   │           └── config/      # Конфигурация
│   ├── resources/
│   │   ├── application.properties  # Настройки приложения
│   │   ├── db/migration/           # SQL миграции
│   │   └── templates/              # HTML
└── test/
└── java/
└── com/
└── lab/
├── controller/  # Тесты контроллеров
└── service/     # Тесты сервисов
====
Установка и запуск
Требования:
Java 17+
Maven 3.8+
PostgreSQL 14+
Git
Шаг 1: Клонирование репозитория
bash
git clone https://github.com/vladislavantoniv/ProbnoeZadaniePrakS3.git
cd lab-information-system
Шаг 2: Настройка базы данных
Создайте базу данных в PostgreSQL:
sql
CREATE DATABASE lab_system_new;
Настройте параметры подключения в application.properties:

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/lab_system_new
spring.datasource.username=postgres
spring.datasource.password=password
Шаг 3: Сборка и запуск
bash
mvn clean package
mvn spring-boot:run
адрес: http://localhost:8080

📡 API Endpoints
Пациенты (/api/patients)
GET /api/patients - список пациентов с пагинацией

GET /api/patients/{id} - пациент по ID

POST /api/patients - создать пациента

PUT /api/patients/{id} - обновить пациента

DELETE /api/patients/{id} - удалить пациента

GET /api/patients/search - поиск пациентов

Заявки (/api/orders)
GET /api/orders - список заявок

GET /api/orders/{id} - заявка по ID

POST /api/orders - создать заявку

PUT /api/orders/{id} - обновить заявку

PUT /api/orders/{id}/status - изменить статус
ДОБАВИТЬ УДАЛЕНИЕ
GET /api/orders/patient/{patientId} - заявки пациента

Типы анализов (/api/test-types)
GET /api/test-types - список типов анализов

GET /api/test-types/{id} - тип по ID

POST /api/test-types - создать тип

PUT /api/test-types/{id} - обновить тип

DELETE /api/test-types/{id} - удалить тип

Анализы (/api/tests)
GET /api/tests - список анализов

GET /api/tests/{id} - анализ по ID

POST /api/tests - создать анализ

PUT /api/tests/{id}/result - добавить результат

GET /api/tests/order/{orderId} - анализы заявки

Веб-интерфейс
Доступные страницы:
/ - главная страница со статистикой
/web/patients - управление пациентами
/web/orders - управление заявками
/web/test-types - управление типами анализов
/web/tests - управление анализами и результатами
Функции веб-интерфейса:
Добавление/редактирование/удаление записей
Пагинация таблиц
Валидация форм в реальном времени
Отображение статусов (цветовые индикаторы)
Поиск и фильтрация

Тестирование
Запуск тестов:
bash
mvn test

Структура тестов:
PatientServiceTest - тесты сервиса пациентов 
OrderServiceTest - тесты сервиса заявок 
TestTypeServiceTest - тесты сервиса типов анализов 
AnalysisServiceTest - тесты сервиса анализов 
LabInformationSystemApplicationTests - базовый тест приложения
OrderControllerTest
Тестовая конфигурация:
Используется H2 in-memory база данных
Автоматическое применение миграций
Миграции базы данных
Система использует Flyway для управления схемой БД:
Миграции:
V1__Create_tables.sql - создание основных таблиц
V2__Insert_initial_data.sql - начальные тестовые данные
V3__Add_triggers_for_updated_at.sql - триггеры для временных меток

Таблицы:
patients - пациенты
orders - заявки на анализы
test_types - типы анализов
tests - анализы

Конфигурация
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/lab_system_new
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=false
spring.flyway.clean-on-validation-error=true
spring.jackson.serialization.fail-on-empty-beans=false
spring.jackson.serialization.write-dates-as-timestamps=false
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
logging.level.com.lab=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.web=DEBUG
server.error.include-message=always
server.error.include-binding-errors=always
spring.jpa.open-in-view=false
pagination.default-page=0
pagination.default-size=10
pagination.default-sort-direction=asc
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

Тестовая конфигурация (application-test.properties):
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=false

Документация API
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI: http://localhost:8080/v3/api-docs