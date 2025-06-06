package cz.binaryburst.example

import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityIdNotFoundException
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.exception.EntityValidationException
import cz.binaryburst.generic.exception.model.ErrorResponse
import cz.binaryburst.generic.exception.util.ErrorResponseUtils
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * This is an example of how an application should implement exception handling
 * when using the generic-crud-helper library. This handler captures the exceptions
 * thrown by the library and translates them into appropriate HTTP responses.
 */
@ControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Handles EntityNotFoundException from the library
     */
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        ex: EntityNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Entity not found: ${ex.message}")

        val errorResponse = ErrorResponseUtils.fromEntityNotFoundException(ex, request.requestURI)
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    /**
     * Handles EntityIdAlreadyExistException from the library
     */
    @ExceptionHandler(EntityIdAlreadyExistException::class)
    fun handleEntityIdAlreadyExistException(
        ex: EntityIdAlreadyExistException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Entity ID already exists: ${ex.message}")

        val errorResponse = ErrorResponseUtils.fromEntityIdAlreadyExistException(ex, request.requestURI)
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    /**
     * Handles EntityIdNotFoundException from the library
     */
    @ExceptionHandler(EntityIdNotFoundException::class)
    fun handleEntityIdNotFoundException(
        ex: EntityIdNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Entity ID not found: ${ex.message}")

        val errorResponse = ErrorResponseUtils.fromEntityIdNotFoundException(ex, request.requestURI)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles EntityValidationException from the library
     */
    @ExceptionHandler(EntityValidationException::class)
    fun handleEntityValidationException(
        ex: EntityValidationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Entity validation failed: ${ex.message}")

        val errorResponse = ErrorResponseUtils.fromEntityValidationException(ex, request.requestURI)

        // Add validation errors to the response if available
        if (ex.errors.isNotEmpty()) {
            val detailedResponse = errorResponse.copy(
                details = mapOf("validationErrors" to ex.errors)
            )
            return ResponseEntity(detailedResponse, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Example of an application-specific exception handler
     */
    @ExceptionHandler(CustomBusinessException::class)
    fun handleCustomBusinessException(
        ex: CustomBusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Business logic error: ${ex.message}", ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
            error = "Business Logic Error",
            message = ex.message ?: "A business logic error occurred",
            path = request.requestURI,
            details = mapOf("businessCode" to ex.errorCode)
        )

        return ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Fallback handler for all other exceptions
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception", ex)

        val errorResponse = ErrorResponseUtils.fromException(ex, request.requestURI)
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

/**
 * Example of an application-specific exception that can be handled alongside
 * the library exceptions
 */
class CustomBusinessException(
    override val message: String,
    val errorCode: String
) : RuntimeException(message)