package com.gusbarros.blockcalls.domain.repository

/**
 * Repository interface for contact-related operations.
 *
 * This interface defines the contract for checking if phone numbers
 * exist in the device's contact list. Implementation must handle
 * normalization and ContentProvider queries.
 */
interface ContactRepository {

    /**
     * Checks if a phone number exists in the device's contacts.
     *
     * @param phoneNumber The phone number to validate (can be in any format)
     * @return true if the number exists in contacts, false otherwise
     */
    suspend fun isNumberInContacts(phoneNumber: String): Boolean
}
