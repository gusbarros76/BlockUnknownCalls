package com.gusbarros.blockcalls.presentation.home

import android.Manifest
import android.app.Application
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gusbarros.blockcalls.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val isActive: Boolean = false,
    val blockedCount: Int = 0
)

class HomeViewModel(
    application: Application,
    private val callHistoryRepository: CallHistoryRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        checkStatus()
        observeBlockedCount()
    }

    fun checkStatus() {
        viewModelScope.launch {
            val context = getApplication<Application>()

            val hasContacts = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val hasPhoneState = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            val hasCallLog = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED

            val hasRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = context.getSystemService(RoleManager::class.java)
                roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) ?: false
            } else {
                false
            }

            _state.value = _state.value.copy(
                isActive = hasContacts && hasPhoneState && hasCallLog && hasRole
            )
        }
    }

    private fun observeBlockedCount() {
        viewModelScope.launch {
            callHistoryRepository.getBlockedCount().collect { count ->
                _state.value = _state.value.copy(blockedCount = count)
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
