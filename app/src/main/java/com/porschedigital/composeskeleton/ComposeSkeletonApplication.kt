package com.porschedigital.composeskeleton

import android.app.Application
import com.porschedigital.composeskeleton.data.VehicleControlsRepository

class ComposeSkeletonApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VehicleControlsRepository.initialize(this)
    }
}