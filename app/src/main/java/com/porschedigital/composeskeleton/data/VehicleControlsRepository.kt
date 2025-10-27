package com.porschedigital.composeskeleton.data

import android.content.Context
import com.porschedigital.composeskeleton.ui.VehicleControl
import com.porschedigital.composeskeleton.ui.VehicleControls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn

sealed class SubscriptionEvent {
    data class VehicleControlsUpdate(val update: VehicleControls) : SubscriptionEvent()
    data class SubscriptionError(val modelPath: String, val error: Throwable) : SubscriptionEvent()
}

object VehicleControlsRepository {
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    lateinit var subscriptionEvents: Flow<SubscriptionEvent>
        private set

    fun initialize(context: Context) {
        initializeSubscriptionFlows()
    }

    private fun initializeSubscriptionFlows() {
        // Prepare the individual flows for each subscription.
        val notificationResponseFlow = createSubscriptionFlow()
            .map<VehicleClient.SubscribePropertyResponse, SubscriptionEvent> { subscriptionMsg ->
                // Map the subscription message to a VehicleControlsUpdate event
                SubscriptionEvent.VehicleControlsUpdate(
                    VehicleControls(
                        controls = listOf(
                            VehicleControl(
                                name = "Driver",
                                value = 2
                            ),
                            VehicleControl(
                                name = "Passenger",
                                value = 2
                            )
                        )
                    )
                )
            }.catch { e ->
                emit(SubscriptionEvent.SubscriptionError("vehicle/controls", e))
            }

        subscriptionEvents = merge(notificationResponseFlow /*, more flows per subscription */)
            .flowOn(Dispatchers.IO)
            .shareIn(repositoryScope, SharingStarted.WhileSubscribed(5000), 1)
    }

    private fun createSubscriptionFlow(): Flow<VehicleClient.SubscribePropertyResponse> {
        return VehicleClient.subscribe()
            .catch { e: Throwable ->
                // Errors from the client can be caught here and transformed into error events upstream
                throw e // Re-throw to be handled by the collector's catch
            }
    }

    suspend fun requestVehicleControls(
    ) {
        publishCommand()
    }

    private suspend fun publishCommand() {
        try {
            val response = VehicleClient.publish(
            )
        } catch (e: Exception) {
            // Handle publish error
        }
    }
}

// TODO: Replace with actual implementation
object VehicleClient {
    data class SubscribePropertyResponse(val data: String = "")

    fun subscribe(): Flow<SubscribePropertyResponse> = flow {
        while (true) {
            emit(SubscribePropertyResponse("dummy data"))
            delay(1000)
        }
    }

    suspend fun publish() {
        delay(100) // Simulate network call
    }
}