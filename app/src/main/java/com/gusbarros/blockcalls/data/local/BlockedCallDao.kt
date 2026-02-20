package com.gusbarros.blockcalls.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallDao {
    @Insert
    suspend fun insert(call: BlockedCallEntity)

    @Query("SELECT COUNT(*) FROM blocked_calls")
    fun getCount(): Flow<Int>
}
