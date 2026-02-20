package com.gusbarros.blockcalls.domain.usecase

import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository

class RecordBlockedCallUseCase(
    private val callHistoryRepository: CallHistoryRepository
) {
    suspend operator fun invoke() {
        callHistoryRepository.recordBlockedCall()
    }
}
