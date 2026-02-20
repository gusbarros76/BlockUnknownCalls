package com.gusbarros.blockcalls.domain.usecase

import com.gusbarros.blockcalls.domain.repository.ContactRepository

/**
 * Use case for validating if a phone number belongs to a contact.
 *
 * This is the core business logic that determines whether an incoming
 * call should be allowed or blocked based on contact presence.
 */
class ValidateContactUseCase(
    private val contactRepository: ContactRepository
) {

    /**
     * Validates if the given phone number is in the user's contacts.
     *
     * @param phoneNumber The phone number to validate
     * @return true if the number is in contacts (allow call), false otherwise (block call)
     */
    suspend operator fun invoke(phoneNumber: String): Boolean {
        // Delegação para repository que fará normalização e query
        return contactRepository.isNumberInContacts(phoneNumber)
    }
}
