package cz.binaryburst.generic.exception.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * Standardized model for error responses.
 * Applications using this library can use this model to create consistent error responses.
 *
 * @property status HTTP status code
 * @property error Type of error
 * @property message Error message
 * @property path Path where the error occurred
 * @property timestamp Timestamp when the error occurred
 * @property details Additional error information
 */
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val details: Map<String, Any> = emptyMap()
) : Serializable