package com.zorenkonte.tibeepost.ui

interface TibeeActions {
    fun sendTest()
    fun previewChime()
    fun generateToken()
    fun clearToken()
    fun toggleAutostart(enabled: Boolean)
    fun changeWidth(percent: Int)
    fun changeDuration(seconds: Int)
    fun changeDim(opacity: Float)
    fun chooseBackground(hex: String)
    fun chooseTextColor(hex: String)
    fun chooseAccent(hex: String)
    fun choosePosition(wire: String)
    fun chooseSound(wire: String)
    fun openOverlaySettings()
    fun completeOnboarding()
    fun resetOnboarding()

    object None : TibeeActions {
        override fun sendTest() {}
        override fun previewChime() {}
        override fun generateToken() {}
        override fun clearToken() {}
        override fun toggleAutostart(enabled: Boolean) {}
        override fun changeWidth(percent: Int) {}
        override fun changeDuration(seconds: Int) {}
        override fun changeDim(opacity: Float) {}
        override fun chooseBackground(hex: String) {}
        override fun chooseTextColor(hex: String) {}
        override fun chooseAccent(hex: String) {}
        override fun choosePosition(wire: String) {}
        override fun chooseSound(wire: String) {}
        override fun openOverlaySettings() {}
        override fun completeOnboarding() {}
        override fun resetOnboarding() {}
    }
}
