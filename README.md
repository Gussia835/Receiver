# Publisher-change-food-card

<details>
  <summary> Нажмите, чтобы посмотреть структуру папок </summary>

```text
src/main/java/org/example/
├── config/          # Конфигурации (Properties, Multi-DataSource)
├── controller/      # Точки входа (REST, gRPC, Scheduled Tasks)
├── service/         # Бизнес-логика (FileReceiverService, SaverDAO)
├── repository/      # Слои доступа к данным (JPA Repositories)
├── models/          # JPA Entity классы (PomFile, GruVistaTab, PomUnit)
├── builders/        # Паттерн Builder для маппинга DTO в Entity
└── utils/           # Утилиты (FileManager, EnrollValidator, EnrollParserVisitor)
```
</details>

## technology 

- Java 21
- Spring Boot 3.2.5 (Web, Data JPA, Scheduling)
- Базы данных: PostgreSQL (POM), Oracle (GRU)
- gRPC 1.60.0 + Protobuf 3.25.1
- Testcontainers 
- Apache Commons Lang3, Datafaker, Lombok
- Gradle, Docker, Docker Compose
