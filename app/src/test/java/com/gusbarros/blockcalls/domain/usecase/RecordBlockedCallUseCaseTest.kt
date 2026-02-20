package com.gusbarros.blockcalls.domain.usecase

import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RecordBlockedCallUseCaseTest {

    private lateinit var callHistoryRepository: CallHistoryRepository
    private lateinit var useCase: RecordBlockedCallUseCase

    @Before
    fun setUp() {
        callHistoryRepository = mockk()
        useCase = RecordBlockedCallUseCase(callHistoryRepository)
    }

    @Test
    fun `invoke calls recordBlockedCall on repository`() = runTest {
        coJustRun { callHistoryRepository.recordBlockedCall() }

        useCase()

        coVerify(exactly = 1) { callHistoryRepository.recordBlockedCall() }
    }
}
