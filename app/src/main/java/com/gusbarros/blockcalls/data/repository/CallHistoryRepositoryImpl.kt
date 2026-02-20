package com.gusbarros.blockcalls.data.repository

import com.gusbarros.blockcalls.data.local.BlockedCallDao
import com.gusbarros.blockcalls.data.local.BlockedCallEntity
import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.Flow

class CallHistoryRepositoryImpl(
    private val dao: BlockedCallDao
) : CallHistoryRepository {

    override suspend fun recordBlockedCall() {
        dao.insert(BlockedCallEntity())
    }

    override fun getBlockedCount(): Flow<Int> = dao.getCount()
}
