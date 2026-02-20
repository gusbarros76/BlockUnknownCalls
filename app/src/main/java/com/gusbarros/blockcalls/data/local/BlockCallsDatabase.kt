package com.gusbarros.blockcalls.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BlockedCallEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BlockCallsDatabase : RoomDatabase() {
    abstract fun blockedCallDao(): BlockedCallDao
}
