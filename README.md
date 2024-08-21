
# Generic Spring Boot Kotlin Library (Generic crud helper)

## Overview

This library provides a set of generic components, such as controllers, services, repositories, and mappers, to simplify the development of CRUD operations in Spring Boot applications. It is designed to be highly reusable and can be easily integrated into any Spring Boot project.

## Features

- **Generic CRUD Operations**: Abstract base classes for controllers, services, and repositories.
- **Custom Exception Handling**: Ready-to-use exceptions for common scenarios like entity not found, ID already exists, etc.
- **Model and DTO Mapping**: Interface for mapping between models, DTOs, and entities.
- **Validation**: Built-in validation logic to ensure data integrity.

## Installation

To include this library in your Spring Boot project, you'll need to set up access to the private repository on GitHub via JitPack. Follow these steps:

1. **Create a `gradle.properties` file** in your project's root directory (if you don't already have one). Add your GitHub username and a GitHub token:

    ```properties
    GITHUB_USERNAME=yourGithubUsername
    GITHUB_TOKEN=yourGithubToken
    ```

   Replace `yourGithubUsername` with your GitHub username and `yourGithubToken` with your personal access token. You can generate a GitHub token from your GitHub account settings.

2. **Configure `build.gradle.kts`** to include the JitPack repository with credentials:

    ```kotlin
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: findProperty("GITHUB_USERNAME") as String? ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: findProperty("GITHUB_TOKEN") as String? ?: ""
            }
        }
    }

    dependencies {
        implementation("com.github.BinaryBurstTech:your-library-name:version")
    }
    ```

   Replace `your-library-name` with the actual name of your library repository and `version` with the appropriate version number.

## Usage

### 1. Defining Your Entities and DTOs

To use this library, define your entity, DTOs, and model by extending the base classes provided by the library.

**Entity Example:**

```kotlin
@Entity
class YourEntity(
    @Id
    override val id: Long,
    val name: String
) : BaseEntity<Long>()
```

**DTO Examples:**

```kotlin
data class YourInsertDto(
    override var id: Long?,
    val name: String
) : BaseInsertDto<Long>()

data class YourOutputDto(
    override val id: Long,
    val name: String
) : BaseOutputDto<Long>()
```

### 2. Creating a Mapper

Implement the `IDataMapper` interface to handle conversions between your DTOs, models, and entities.

```kotlin
class YourEntityMapper : IDataMapper<YourInsertDto, YourOutputDto, YourModel, YourEntity, Long> {
    override fun convertDtoToModel(dto: YourInsertDto): YourModel {
        return YourModel(
            id = dto.id,
            name = dto.name
        )
    }
    
    override fun convertModelToDtoOut(model: YourModel): YourOutputDto {
        return YourOutputDto(
            id = model.id,
            name = model.name
        )
    }

    // Implement other methods...
}
```

### 3. Creating a Service

Extend `BaseService` to handle the business logic for your entities.

```kotlin
@Service
class YourEntityService(
    repository: YourEntityRepository,
    mapper: YourEntityMapper
) : BaseService<Long, YourInsertDto, YourOutputDto, YourModel, YourEntity, YourEntityRepository, YourEntityMapper>(repository, mapper)
```

### 4. Creating a Controller

Use the `BaseController` to quickly set up a REST API for your entity.

```kotlin
@RestController
@RequestMapping("/your-entities")
class YourEntityController(
    service: YourEntityService,
    mapper: YourEntityMapper
) : BaseController<Long, YourInsertDto, YourOutputDto, YourModel, YourEntity, YourEntityMapper, YourEntityRepository, YourEntityService>(service, mapper)
```

### 5. Exception Handling

The library includes custom exceptions for common scenarios. These exceptions can be used or extended as needed.

**Example:**

```kotlin
if (someConditionFails) {
    throw EntityValidationException("Validation failed for YourEntity")
}
```

## Testing

To ensure your application works correctly with the library, write unit and integration tests. You can use Spring Boot's testing support to test the functionality of your services, controllers, and repositories.

**Example:**

```kotlin
@SpringBootTest
class YourEntityServiceTests {

    @Autowired
    lateinit var yourEntityService: YourEntityService

    @Test
    fun `test create entity`() {
        val dto = YourInsertDto(id = null, name = "Test Entity")
        val result = yourEntityService.create(dto)
        assertNotNull(result.id)
    }
}
```

## Releasing a New Version

To release a new version of the library:

1. **Create a new Git tag** for the release version. For example:

    ```sh
    git tag v1.0.0
    git push origin v1.0.0
    ```

2. **Check the build log** on [JitPack.io](https://jitpack.io) to ensure that the new version was successfully built and is available for use.

## Contributing

Since this is a private repository, contributions are managed within the team. Please create a feature branch, make your changes, and then open a pull request for review.

## License

This project is a private repository and is not licensed for public use.

## Contact

If you have any questions or need further assistance, feel free to open an issue or contact us directly.
