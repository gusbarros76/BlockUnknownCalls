package com.gusbarros.blockcalls.presentation.onboarding

import android.Manifest
import android.app.Application
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingState(
    val hasContactsPermission: Boolean = false,
    val hasPhoneStatePermission: Boolean = false,
    val hasCallLogPermission: Boolean = false,
    val hasCallScreeningRole: Boolean = false,
    val isComplete: Boolean = false
)

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    init {
        checkStatus()
    }

    fun checkStatus() {
        viewModelScope.launch {
            val context = getApplication<Application>()

            val hasContacts = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            val hasPhoneState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            val hasCallLog = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED

            // RoleManager só existe API 29+
            val hasRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = context.getSystemService(RoleManager::class.java)
                roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) ?: false
            } else {
                false
            }

            val allPermissionsGranted = hasContacts && hasPhoneState && hasCallLog
            val isComplete = allPermissionsGranted && hasRole

            _state.value = OnboardingState(
                hasContactsPermission = hasContacts,
                hasPhoneStatePermission = hasPhoneState,
                hasCallLogPermission = hasCallLog,
                hasCallScreeningRole = hasRole,
                isComplete = isComplete
            )
        }
    }

    companion object {
        private const val TAG = "OnboardingViewModel"
    }
}
