package com.porschedigital.composeskeleton.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.porschedigital.composeskeleton.data.SubscriptionEvent
import com.porschedigital.composeskeleton.data.VehicleControlsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Generic vehicle control data class
data class VehicleControl(
    val name: String,
    val value: Int
)

// Container for vehicle controls
data class VehicleControls(
    val controls: List<VehicleControl>
)

class VehicleControlsViewModel(
    private val repository: VehicleControlsRepository
): ViewModel() {
    private val TAG = VehicleControlsViewModel::class.java.simpleName

    data class VehicleControlsUiState(
        val vehicleControls: VehicleControls = VehicleControls(
            controls = listOf(
                VehicleControl(name = "Driver", value = 2),
                VehicleControl(name = "Passenger", value = 2)
            )
        )
    )
    
    private val _vehicleControlsUiState = MutableStateFlow(VehicleControlsUiState())
    val vehicleControlsUiState: StateFlow<VehicleControlsUiState> = _vehicleControlsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.subscriptionEvents.collect { event ->
                Log.d(TAG, "Received subscription event: $event")
                when (event) {
                    // TODO: Implement handling for vehicle controls updates
                    is SubscriptionEvent.SubscriptionError -> handleSubscriptionError(event.error)
                    is SubscriptionEvent.VehicleControlsUpdate -> handleControlsUpdate(event.update)
                }
            }
        }
    }

    private fun handleControlsUpdate(result: VehicleControls) {
        Log.d(TAG, "Handling controls update: $result")
        _vehicleControlsUiState.update { currentState ->
            currentState.copy(vehicleControls = result)
        }
    }

    private fun handleSubscriptionError(error: Throwable) {
        Log.e(TAG, "Subscription error for $error: ${error.message}")
    }

    fun handleIntent(intent: VehicleControlsViewIntent) = with(intent) {
        Log.d(TAG, "Handling intent: $this")
        when (this) {
            is VehicleControlsViewIntent.SetControlValue -> {
                Log.d(TAG, "Control Value Change Requested: Control=${controlName}, Value=${value}")
                // TODO: Use repository to send control value change over vehicle client instead
                viewModelScope.launch {
                    repository.requestVehicleControls()
                }
                // For now, this just directly updates the UI state
                updateControlValue(controlName, value)
            }
        }
    }

    private fun updateControlValue(
        controlName: String,
        value: Int
    ) {
        val currentState = _vehicleControlsUiState.value
        val updatedControls = currentState.vehicleControls.controls.map { control ->
            if (control.name == controlName) {
                control.copy(value = value)
            } else {
                control
            }
        }
        _vehicleControlsUiState.value = currentState.copy(
            vehicleControls = VehicleControls(controls = updatedControls)
        )
    }

    sealed interface VehicleControlsViewIntent {
        data class SetControlValue(
            val controlName: String,
            val value: Int
        ) : VehicleControlsViewIntent
    }
}

class VehicleControlsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleControlsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehicleControlsViewModel(VehicleControlsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}