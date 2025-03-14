package cz.binaryburst.generic.docs

/**
 * # Exception Handling in Applications
 *
 * This library defines several domain-specific exceptions and provides utility methods
 * to help applications handle them in a consistent way. However, the actual exception
 * handling logic should be implemented by the application using this library.
 *
 * ## Recommended approach
 *
 * It is recommended that applications using this library implement their own
 * global exception handler using Spring's @ControllerAdvice mechanism. Here's an example:
 *
 * ```kotlin
 * @ControllerAdvice
 * class GlobalExceptionHandler {
 *
 *     @ExceptionHandler(EntityNotFoundException::class)
 *     fun handleEntityNotFoundException(
 *         ex: EntityNotFoundException,
 *         request: HttpServletRequest
 *     ): ResponseEntity<ErrorResponse> {
 *         val errorResponse = ErrorResponseUtils.fromEntityNotFoundException(ex, request.requestURI)
 *         return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
 *     }
 *
 *     @ExceptionHandler(EntityIdAlreadyExistException::class)
 *     fun handleEntityIdAlreadyExistException(
 *         ex: EntityIdAlreadyExistException,
 *         request: HttpServletRequest
 *     ): ResponseEntity<ErrorResponse> {
 *         val errorResponse = ErrorResponseUtils.fromEntityIdAlreadyExistException(ex, request.requestURI)
 *         return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
 *     }
 *
 *     @ExceptionHandler(EntityIdNotFoundException::class)
 *     fun handleEntityIdNotFoundException(
 *         ex: EntityIdNotFoundException,
 *         request: HttpServletRequest
 *     ): ResponseEntity<ErrorResponse> {
 *         val errorResponse = ErrorResponseUtils.fromEntityIdNotFoundException(ex, request.requestURI)
 *         return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
 *     }
 *
 *     @ExceptionHandler(EntityValidationException::class)
 *     fun handleEntityValidationException(
 *         ex: EntityValidationException,
 *         request: HttpServletRequest
 *     ): ResponseEntity<ErrorResponse> {
 *         val errorResponse = ErrorResponseUtils.fromEntityValidationException(ex, request.requestURI)
 *         return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
 *     }
 *
 *     @ExceptionHandler(Exception::class)
 *     fun handleGeneralException(
 *         ex: Exception,
 *         request: HttpServletRequest
 *     ): ResponseEntity<ErrorResponse> {
 *         val errorResponse = ErrorResponseUtils.fromException(ex, request.requestURI)
 *         return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
 *     }
 * }
 * ```
 *
 * ## About exceptions in this library
 *
 * This library follows these principles for exception handling:
 *
 * 1. **Clear exception types**: Each failure scenario has a dedicated exception type
 * 2. **Rich exception data**: Exceptions include useful data (IDs, entity types, etc.)
 * 3. **No swallowed exceptions**: Exceptions propagate to application layer for handling
 * 4. **Consistent logging**: All relevant events are logged but exceptions are not suppressed
 *
 * ## Main exception types
 *
 * - `EntityNotFoundException`: When an entity with a given ID is not found
 * - `EntityIdAlreadyExistException`: When trying to create an entity with an ID that already exists
 * - `EntityIdNotFoundException`: When an operation requires an ID but none is provided
 * - `EntityValidationException`: When validation fails for an entity
 */
class ExceptionHandlingDocumentation {
    // This class exists only to hold documentation and isn't meant to be instantiated
}