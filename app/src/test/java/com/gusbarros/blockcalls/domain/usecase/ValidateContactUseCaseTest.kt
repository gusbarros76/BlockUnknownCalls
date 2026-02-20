package com.gusbarros.blockcalls.domain.usecase

import com.gusbarros.blockcalls.domain.repository.ContactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateContactUseCaseTest {

    private lateinit var contactRepository: ContactRepository
    private lateinit var useCase: ValidateContactUseCase

    @Before
    fun setUp() {
        contactRepository = mockk()
        useCase = ValidateContactUseCase(contactRepository)
    }

    @Test
    fun `invoke returns true when number is in contacts`() = runTest {
        coEvery { contactRepository.isNumberInContacts(any()) } returns true

        val result = useCase("11987654321")

        assertTrue(result)
    }

    @Test
    fun `invoke returns false when number is not in contacts`() = runTest {
        coEvery { contactRepository.isNumberInContacts(any()) } returns false

        val result = useCase("99999999999")

        assertFalse(result)
    }

    @Test
    fun `invoke passes phone number through to repository`() = runTest {
        val phoneNumber = "11987654321"
        coEvery { contactRepository.isNumberInContacts(phoneNumber) } returns true

        useCase(phoneNumber)

        coVerify(exactly = 1) { contactRepository.isNumberInContacts(phoneNumber) }
    }
}
