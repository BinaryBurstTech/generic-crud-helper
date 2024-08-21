
# Generic Spring Boot Kotlin Library

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Version](https://img.shields.io/badge/version-1.0.3-blue)
![Downloads](https://img.shields.io/badge/downloads-1%2B-brightgreen)

## Table of Contents
- [Overview](#overview)
- [Motivation](#motivation)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Testing](#testing)
- [Releasing a New Version](#releasing-a-new-version)
- [Limitations](#limitations)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Overview

This library provides a set of generic components, such as controllers, services, repositories, and mappers, to simplify the development of CRUD operations in Spring Boot applications. It is designed to be highly reusable and can be easily integrated into any Spring Boot project.

## Motivation

Developing a consistent and maintainable codebase for CRUD operations across multiple projects can be time-consuming and prone to errors. This library abstracts common CRUD functionality into reusable components, allowing you to focus on the unique business logic of your application rather than repetitive boilerplate code. By standardizing the approach to CRUD operations, you can ensure consistency, reduce bugs, and accelerate development.

## Features

- **Generic CRUD Operations**: Abstract base classes for controllers, services, and repositories.
- **Custom Exception Handling**: Ready-to-use exceptions for common scenarios like entity not found, ID already exists, etc.
- **Model and DTO Mapping**: Interface for mapping between models, DTOs, and entities.
- **Validation**: Built-in validation logic to ensure data integrity.

## Installation

To include this library in your Spring Boot project, you'll need to set up access to the private repository on GitHub via JitPack. Follow these steps:

1. **Create a `gradle.properties` file** in your project's root directory:

    ```properties
    GITHUB_USERNAME=yourGithubUsername
    GITHUB_TOKEN=yourGithubToken
    ```

2. **Configure `build.gradle.kts`**:

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

## Usage

### 1. Defining Your Entities and DTOs

To use this library, define your entity, DTOs, and model by extending the base classes provided by the library.

```kotlin
@Entity
class YourEntity(
    @Id
    override val id: Long,
    val name: String
) : BaseEntity<Long>()
```

### 2. Creating a Mapper

```kotlin
class YourEntityMapper : IDataMapper<YourInsertDto, YourOutputDto, YourModel, YourEntity, Long> {
    override fun convertDtoToModel(dto: YourInsertDto): YourModel {
        // conversion logic
    }
    
    override fun convertModelToDtoOut(model: YourModel): YourOutputDto {
        // conversion logic
    }
}
```

### 3. Creating a Service

```kotlin
@Service
class YourEntityService(
    repository: YourEntityRepository,
    mapper: YourEntityMapper
) : BaseService<Long, YourInsertDto, YourOutputDto, YourModel, YourEntity, YourEntityRepository, YourEntityMapper>(repository, mapper)
```

### 4. Creating a Controller

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

```kotlin
if (someConditionFails) {
    throw EntityValidationException("Validation failed for YourEntity")
}
```

## Testing

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

1. **Create a new Git tag**:

    ```sh
    git tag v1.0.0
    git push origin v1.0.0
    ```

2. **Check the build log** on [JitPack.io](https://jitpack.io) to ensure the new version is available.

## Limitations

- **Complex Business Logic**: While the library abstracts common CRUD operations, it may not be suitable for entities with highly complex business rules that require custom implementations beyond simple CRUD.
- **Customization**: The generic nature of this library means that it may require additional customization to fit unique project needs, especially when dealing with legacy systems or non-standard data structures.
- **Performance**: The abstraction layers could introduce slight performance overhead, which might be noticeable in applications with very high throughput requirements. In such cases, fine-tuning or bypassing the library in critical paths might be necessary.
- **Testing**: The provided base classes simplify development but might complicate unit testing for developers unfamiliar with dependency injection and Spring Boot testing practices.

## Contributing

Since this is a private repository, contributions are managed within the team. Please create a feature branch, make your changes, and then open a pull request for review.

## License

This project is a private repository and is not licensed for public use.

## Contact

If you have any questions or need further assistance, feel free to open an issue or contact us directly.
