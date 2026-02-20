package com.gusbarros.blockcalls.domain.model

data class BlockedCall(
    val id: Long = 0,
    val blockedAt: Long = System.currentTimeMillis()
)
