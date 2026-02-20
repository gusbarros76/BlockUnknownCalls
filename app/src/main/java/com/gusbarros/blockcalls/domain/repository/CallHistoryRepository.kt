package com.gusbarros.blockcalls.domain.repository

import kotlinx.coroutines.flow.Flow

interface CallHistoryRepository {
    suspend fun recordBlockedCall()
    fun getBlockedCount(): Flow<Int>
}
