package cz.binaryburst.generic.dto

/**
 * Data class representing a paginated response.
 *
 * @param T The type of content in the page.
 * @property content The list of items in the current page.
 * @property totalElements The total number of elements across all pages.
 * @property totalPages The total number of pages.
 * @property pageNumber The current page number (0-based).
 * @property pageSize The number of items per page.
 * @property isLast Whether this is the last page.
 */
data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val isLast: Boolean
)