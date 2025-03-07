# Generic CRUD Helper

A comprehensive library that provides standardized base implementations for CRUD operations in Spring Boot applications. This library helps eliminate boilerplate code and provides a consistent approach to building data access layers in your applications.

[![](https://jitpack.io/v/BinaryBurstTech/generic-crud-helper.svg)](https://jitpack.io/#BinaryBurstTech/generic-crud-helper)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Features

- **Base CRUD Operations**: Complete set of standardized Create, Read, Update, Delete operations
- **Orderable Entities**: Support for entities that need to maintain position/order
- **Embedded Entities**: Support for complex entity relationships including embedded entities
- **Advanced Mapper System**: Type-safe conversion between DTOs, Models, and Entities
- **Pagination**: Built-in pagination support using Spring's Pageable interface
- **Exception Handling**: Standardized exceptions for common CRUD scenarios
- **Comprehensive Logging**: Detailed logging throughout the entire CRUD lifecycle
- **Extensive Testing**: Fully tested with real database scenarios
- **Spring Integration**: Seamless integration with Spring Boot applications

## Getting Started

### Installation

Add the JitPack repository to your build file:

```kotlin
// build.gradle.kts
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.BinaryBurstTech:generic-crud-helper:1.0.5")
}
```

### Requirements

- Java 21+
- Spring Boot 3.x
- Kotlin 1.9+

## Usage

The library provides several abstract base components that you can extend to quickly implement CRUD functionality:

### 1. Define your Entity

```kotlin
@Entity
@Table(name = "products")
class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    
    @Column(name = "name")
    var name: String = "",
    
    @Column(name = "price")
    var price: BigDecimal = BigDecimal.ZERO
) : BaseEntity<Long>()
```

### 2. Create DTOs

```kotlin
data class ProductDtoInput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("price") var price: BigDecimal = BigDecimal.ZERO
) : BaseDtoInput<Long>()

data class ProductDtoOutput @JsonCreator constructor(
    @JsonProperty("id") override var id: Long = 0,
    @JsonProperty("name") var name: String = "",
    @JsonProperty("price") var price: BigDecimal = BigDecimal.ZERO
) : BaseDtoOutput<Long>()
```

### 3. Define your Model

```kotlin
data class ProductModel(
    override var id: Long,
    var name: String,
    var price: BigDecimal
) : BaseModel<Long>()
```

### 4. Create a Repository

```kotlin
@Repository
interface ProductRepository : BaseRepository<ProductEntity, Long>
```

### 5. Create a Mapper

```kotlin
@Component
class ProductMapper : IBaseMapper<ProductDtoInput, ProductDtoOutput, ProductModel, ProductEntity, Long> {
    override fun convertDtoToModel(dto: ProductDtoInput): ProductModel {
        return ProductModel(
            id = dto.id,
            name = dto.name,
            price = dto.price
        )
    }
    
    // Implement other required mapper methods...
}
```

### 6. Create a Service

```kotlin
@Service
class ProductService(
    repository: ProductRepository,
    mapper: ProductMapper
) : BaseService<
        Long,
        ProductDtoInput,
        ProductDtoOutput,
        ProductModel,
        ProductEntity,
        ProductRepository,
        ProductMapper
        >(
    repository = repository,
    mapper = mapper
)
```

### 7. Create a Controller

```kotlin
@RestController
@RequestMapping("/api/products")
class ProductController(
    service: ProductService,
    mapper: ProductMapper
) : BaseController<
        Long,
        ProductDtoInput,
        ProductDtoOutput,
        ProductModel,
        ProductEntity,
        ProductMapper,
        ProductRepository,
        ProductService
        >(
    service = service,
    mapper = mapper
) {
    @GetMapping
    override fun getAll(pageable: Pageable) = super.getAll(pageable)
    
    @GetMapping("/{id}")
    override fun get(@PathVariable id: Long) = super.get(id)
    
    @PostMapping
    override fun create(@RequestBody dto: ProductDtoInput) = super.create(dto)
    
    @PutMapping("/{id}")
    override fun update(@PathVariable id: Long, @RequestBody dto: ProductDtoInput) = super.update(id, dto)
    
    @DeleteMapping("/{id}")
    override fun delete(@PathVariable id: Long) = super.delete(id)
}
```

## Working with Orderable Entities

For entities that need to maintain position/order, use the `OrderableEntity`, `OrderableDtoInput`, `OrderableDtoOutput`, and `OrderableModel` base classes:

```kotlin
@Entity
@Table(name = "menu_items")
class MenuItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    
    @Column(name = "name")
    var name: String = "",
    
    @Column(name = "position")
    override var position: Int
) : OrderableEntity<Long>()
```

Then use `OrderableService` for managing the entity's position:

```kotlin
@Service
class MenuItemService(
    repository: MenuItemRepository,
    mapper: MenuItemMapper,
    positionableRepository: IOrderablePositionableRepository<MenuItemParams, MenuItemEntity>
) : OrderableService<
        Long,
        MenuItemParams,
        MenuItemDtoInput,
        MenuItemDtoOutput,
        MenuItemModel,
        MenuItemEntity,
        MenuItemRepository,
        MenuItemMapper
        >(
    repository = repository,
    mapper = mapper,
    positionableRepository = positionableRepository
)
```

## Architecture

The library follows a layered architecture:

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic and transaction management
3. **Repository Layer**: Data access layer for CRUD operations
4. **Model Layer**: Business domain models
5. **Entity Layer**: JPA entities for database persistence
6. **DTO Layer**: Data transfer objects for API input/output
7. **Mapper Layer**: Handles conversions between layers

Each layer has generic base classes and interfaces that you can extend.

## Advanced Features

### Pagination

All retrieval methods support pagination using Spring's `Pageable` interface:

```kotlin
val page = service.findAll(PageRequest.of(0, 10, Sort.by("name")))
```

### Exception Handling

The library provides standardized exceptions:

- `EntityNotFoundException`: When an entity cannot be found by ID
- `EntityIdNotFoundException`: When an ID is required but not provided
- `EntityIdAlreadyExistException`: When trying to create an entity with an ID that already exists
- `EntityValidationException`: When entity validation fails

## License

This library is available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For support or questions, please open an issue on the GitHub repository.
