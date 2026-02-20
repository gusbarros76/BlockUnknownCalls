package com.gusbarros.blockcalls.data.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.gusbarros.blockcalls.domain.usecase.RecordBlockedCallUseCase
import com.gusbarros.blockcalls.domain.usecase.ValidateContactUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Service that intercepts incoming calls and blocks unknown numbers.
 *
 * This service is invoked by the Android system before a call rings.
 * It validates the caller against the user's contacts and decides
 * whether to allow or reject the call.
 */
class CallScreeningServiceImpl : CallScreeningService() {

    private val validateContactUseCase: ValidateContactUseCase by inject()
    private val recordBlockedCallUseCase: RecordBlockedCallUseCase by inject()

    // SupervisorJob garante que erros em coroutines não crashem o service
    private val serviceScope = CoroutineScope(SupervisorJob())

    override fun onScreenCall(callDetails: Call.Details) {
        // Fail-safe: em caso de erro, sempre permitir chamada (pode ser emergência)
        val defaultResponse = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .build()

        try {
            val phoneNumber = callDetails.handle?.schemeSpecificPart

            if (phoneNumber.isNullOrBlank()) {
                // Número desconhecido/privado - permitir para não bloquear emergências
                Log.d(TAG, "Unknown/private number - allowing call")
                respondToCall(callDetails, defaultResponse)
                return
            }

            // Validação assíncrona com coroutine
            serviceScope.launch {
                try {
                    val isContact = validateContactUseCase(phoneNumber)

                    val response = if (isContact) {
                        // Número está nos contatos - permitir chamada
                        Log.d(TAG, "Contact found - allowing call")
                        CallResponse.Builder()
                            .setDisallowCall(false)
                            .setRejectCall(false)
                            .build()
                    } else {
                        // Número desconhecido - bloquear chamada
                        Log.d(TAG, "Unknown number - blocking call")
                        recordBlockedCallUseCase()
                        CallResponse.Builder()
                            .setDisallowCall(true)
                            .setRejectCall(true)
                            .setSkipCallLog(false)
                            .setSkipNotification(false)
                            .build()
                    }

                    respondToCall(callDetails, response)

                } catch (e: Exception) {
                    // Fail-safe: em caso de erro, permitir chamada
                    Log.e(TAG, "Error validating contact - allowing call", e)
                    respondToCall(callDetails, defaultResponse)
                }
            }

        } catch (e: Exception) {
            // Fail-safe: em caso de erro crítico, permitir chamada
            Log.e(TAG, "Critical error in onScreenCall - allowing call", e)
            respondToCall(callDetails, defaultResponse)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "CallScreeningService"
    }
}
