package com.porschedigital.composeskeleton.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.porschedigital.composeskeleton.ui.VehicleControlsViewModel
import com.porschedigital.composeskeleton.ui.VehicleControl
import com.porschedigital.composeskeleton.ui.VehicleControls

@Composable
fun VehicleControlsScreen(
    modifier: Modifier = Modifier,
    vehicleControlsViewModel: VehicleControlsViewModel = viewModel()
) {
    val vehicleControlsUiState by vehicleControlsViewModel.vehicleControlsUiState.collectAsState()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VehicleControls(
                    vehicleControlsUiState.vehicleControls
                ) {
                    vehicleControlsViewModel.handleIntent(it)
                }
            }
        }
    }
}

@Composable
fun VehicleControls(
    vehicleControls: VehicleControls,
    intentHandler: (VehicleControlsViewModel.VehicleControlsViewIntent) -> Unit
) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vehicle Controls",
            style = TextStyle(fontSize = 20.sp)
        )

        vehicleControls.controls.forEach {
            VehicleControl(
                vehicleControl = it,
                intentHandler = intentHandler
            )
        }
    }
}

@Composable
fun VehicleControl(
    vehicleControl: VehicleControl,
    intentHandler: (VehicleControlsViewModel.VehicleControlsViewIntent) -> Unit
) {
    fun setVehicleControl(offset: Int) {
        val newValue = (vehicleControl.value + offset).coerceIn(0, 10)
        if (newValue != vehicleControl.value) {
            intentHandler(
                VehicleControlsViewModel.VehicleControlsViewIntent.SetControlValue(
                    vehicleControl.name,
                    newValue
                )
            )
        }
    }

    Column(
        modifier = Modifier.wrapContentSize(),
    ) {
        Text(
            text = vehicleControl.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { setVehicleControl(-1) },
                enabled = vehicleControl.value > 0
            ) {
                Text(text = "-")
            }

            Text(
                text = "${vehicleControl.value}",
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Button(
                onClick = { setVehicleControl(1) },
                enabled = vehicleControl.value < 10
            ) {
                Text(
                    text = "+"
                )
            }
        }
    }
}
