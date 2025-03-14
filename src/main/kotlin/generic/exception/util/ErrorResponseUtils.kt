package cz.binaryburst.generic.exception.util

import cz.binaryburst.generic.exception.EntityIdAlreadyExistException
import cz.binaryburst.generic.exception.EntityIdNotFoundException
import cz.binaryburst.generic.exception.EntityNotFoundException
import cz.binaryburst.generic.exception.EntityValidationException
import cz.binaryburst.generic.exception.model.ErrorResponse
import org.springframework.http.HttpStatus

/**
 * Utility class for creating standardized error responses from exceptions.
 *
 * This class is provided as a convenience for applications using this library.
 * Applications are free to implement their own error handling strategy.
 */
object ErrorResponseUtils {

    /**
     * Creates an error response for EntityNotFoundException.
     *
     * @param ex The exception
     * @param path The request path
     * @return Standardized error response
     */
    fun fromEntityNotFoundException(
        ex: EntityNotFoundException,
        path: String
    ): ErrorResponse = ErrorResponse(
        status = HttpStatus.NOT_FOUND.value(),
        error = "Not Found",
        message = ex.message ?: "The requested entity was not found",
        path = path
    )

    /**
     * Creates an error response for EntityIdAlreadyExistException.
     *
     * @param ex The exception
     * @param path The request path
     * @return Standardized error response
     */
    fun fromEntityIdAlreadyExistException(
        ex: EntityIdAlreadyExistException,
        path: String
    ): ErrorResponse = ErrorResponse(
        status = HttpStatus.CONFLICT.value(),
        error = "Conflict",
        message = ex.message ?: "Entity with this ID already exists",
        path = path
    )

    /**
     * Creates an error response for EntityIdNotFoundException.
     *
     * @param ex The exception
     * @param path The request path
     * @return Standardized error response
     */
    fun fromEntityIdNotFoundException(
        ex: EntityIdNotFoundException,
        path: String
    ): ErrorResponse = ErrorResponse(
        status = HttpStatus.BAD_REQUEST.value(),
        error = "Bad Request",
        message = ex.message ?: "Entity ID is missing",
        path = path
    )

    /**
     * Creates an error response for EntityValidationException.
     *
     * @param ex The exception
     * @param path The request path
     * @return Standardized error response
     */
    fun fromEntityValidationException(
        ex: EntityValidationException,
        path: String
    ): ErrorResponse = ErrorResponse(
        status = HttpStatus.BAD_REQUEST.value(),
        error = "Validation Error",
        message = ex.message ?: "Entity validation failed",
        path = path
    )

    /**
     * Creates a generic error response for any exception.
     *
     * @param ex The exception
     * @param path The request path
     * @param status The HTTP status code
     * @param error The error type
     * @return Standardized error response
     */
    fun fromException(
        ex: Exception,
        path: String,
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        error: String = "Internal Server Error"
    ): ErrorResponse = ErrorResponse(
        status = status.value(),
        error = error,
        message = ex.message ?: "An unexpected error occurred",
        path = path
    )
}