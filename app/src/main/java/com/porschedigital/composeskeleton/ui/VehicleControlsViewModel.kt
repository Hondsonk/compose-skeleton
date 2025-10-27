package com.porschedigital.composeskeleton.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Generic vehicle control data class
data class VehicleControl(
    val name: String,
    val value: Int
)

// Container for vehicle controls
data class VehicleControls(
    val controls: List<VehicleControl>
)

class VehicleControlsViewModel(): ViewModel() {
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

    fun handleIntent(intent: VehicleControlsViewIntent) = with(intent) {
        Log.d(TAG, "Handling intent: $this")
        when (this) {
            is VehicleControlsViewIntent.SetControlValue -> {
                Log.d(TAG, "Control Value Change Requested: Control=${controlName}, Value=${value}")
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